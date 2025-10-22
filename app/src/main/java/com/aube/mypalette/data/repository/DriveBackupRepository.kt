package com.aube.mypalette.data.repository

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aube.mypalette.utils.DriveServiceFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.http.FileContent
import com.google.api.services.drive.DriveScopes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class DriveBackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val sp by lazy { context.getSharedPreferences("drive", Context.MODE_PRIVATE) }

    private companion object {
        const val BACKUP_PREFIX = "my_palette_backup_"
        private val TS_FMT = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US)
    }

    // 업로드 목적지 선택지
    sealed class DriveDest {
        /** 기본: appDataFolder(숨김, 앱 전용) */
        data object AppDataFolder : DriveDest()

        /** My Drive (보이는 영역). folderId=null이면 루트로 저장 */
        data class MyDrive(val folderId: String? = null) : DriveDest()
    }

    fun persistAccountName(name: String?) {
        sp.edit().putString("account", name).apply()
    }

    fun restoreAccountName(): String? = sp.getString("account", null)

    fun persistLastBackupTime(ts: Long) {
        sp.edit().putLong("last_backup", ts).apply()
    }

    fun restoreLastBackupTime(): Long? = sp.getLong("last_backup", 0L).takeIf { it > 0L }

    suspend fun signOut(ctx: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA), Scope(DriveScopes.DRIVE_FILE))
            .build()
        GoogleSignIn.getClient(ctx, gso).signOut().await()
    }

    // -------------------- 백업 --------------------
    /**
     * @param accountName 구글 계정 이메일
     * @param dest 업로드 목적지 (기본 AppDataFolder)
     * @param checkpoint 옵셔널: 백업 직전 WAL 체크포인트 등을 수행할 콜백
     */
    suspend fun backupNow(
        accountName: String,
        dest: DriveDest = DriveDest.AppDataFolder,
        checkpoint: (() -> Unit)? = null,
    ): Long = withContext(Dispatchers.IO) {
        val service = DriveServiceFactory.create(context, accountName)

        // (PATCH) 백업 직전 체크포인트 훅
        runCatching { checkpoint?.invoke() }

        // 1) ZIP 만들기
        val zip = makeBackupZip()

        // 2) 업로드
        val meta = com.google.api.services.drive.model.File().apply {
            name = "${BACKUP_PREFIX}${TS_FMT.format(Date())}.zip"
            when (dest) {
                is DriveDest.AppDataFolder -> {
                    parents = listOf("appDataFolder")
                }

                is DriveDest.MyDrive -> {
                    if (dest.folderId != null) parents = listOf(dest.folderId)
                }
            }
            mimeType = "application/zip"
        }
        val media = FileContent("application/zip", zip)

        service.files().create(meta, media)
            .setFields("id,name,createdTime,modifiedTime,size,mimeType,parents")
            .execute()

        persistLastBackupTime(System.currentTimeMillis())
        zip.delete()
        System.currentTimeMillis()
    }

    // -------------------- 복원 --------------------
    /**
     * @param accountName 구글 계정
     * @param from 업로드 위치와 동일하게 지정해야 정확 (기본 AppDataFolder)
     * @param beforeRestoreCloseDb 필수 권장: Room DB close 콜백
     */
    suspend fun restoreLatest(
        accountName: String,
        from: DriveDest = DriveDest.AppDataFolder,
        beforeRestoreCloseDb: (() -> Unit)? = null,
    ) = withContext(Dispatchers.IO) {
        val service = DriveServiceFactory.create(context, accountName)

        val (spaces, qExtra) = when (from) {
            is DriveDest.AppDataFolder -> "appDataFolder" to ""
            is DriveDest.MyDrive -> "drive" to ""
        }

        val list = service.files().list()
            .setSpaces(spaces)
            .setQ(
                """
                name contains '$BACKUP_PREFIX' and 
                (mimeType='application/zip' or mimeType='application/octet-stream') and 
                trashed=false
            """.trimIndent() + qExtra
            )
            .setOrderBy("modifiedTime desc")
            .setPageSize(5)
            .setFields("files(id,name,modifiedTime,size,mimeType)")
            .execute()

        val file = list.files?.firstOrNull()
            ?: error("No backup found in ${from.javaClass.simpleName}")

        // 다운로드
        val dest = File(context.cacheDir, "restore_${System.currentTimeMillis()}.zip")
        BufferedOutputStream(FileOutputStream(dest)).use { out ->
            service.files().get(file.id).executeMediaAndDownloadTo(out)
        }
        require(dest.exists() && dest.length() > 0L) {
            "Downloaded backup is empty (id=${file.id}, name=${file.name})"
        }

        // (PATCH) 복원 전 DB 닫기
        runCatching { beforeRestoreCloseDb?.invoke() }

        // ZIP 적용
        restoreFromZip(dest)
        dest.delete()

        // 재시작 필요 플래그
        sp.edit().putBoolean("db_restored", true).apply()
    }

    // -------------------- 내부 구현 --------------------

    /** (PATCH) /databases 아래의 '모든 파일'을 백업하여 본파일 누락 방지 */
    private fun makeBackupZip(): File {
        val out = File(context.cacheDir, "backup_${System.currentTimeMillis()}.zip")
        ZipOutputStream(BufferedOutputStream(FileOutputStream(out))).use { zos ->
            putAllDatabases(zos)
            putAllAppFiles(zos)
        }
        return out
    }

    private fun putAllDatabases(zos: ZipOutputStream) {
        val dbDir = context.getDatabasePath("dummy").parentFile ?: return
        if (!dbDir.exists()) return
        dbDir.listFiles()?.forEach { f ->
            if (f.isFile) {
                putFile(zos, f, "databases/${f.name}")
            }
        }
    }

    /** 예시: files/images 폴더를 함께 보존한다면 여기서 추가 */
    private fun putAllAppFiles(zos: ZipOutputStream) {
        val imgDir = File(context.filesDir, "images")
        if (imgDir.exists()) {
            imgDir.walkTopDown().filter { it.isFile }.forEach { f ->
                val rel = "files/images/${f.relativeTo(imgDir).invariantSeparatorsPath}"
                putFile(zos, f, rel)
            }
        }
    }

    private fun restoreFromZip(zip: File) {
        ZipInputStream(BufferedInputStream(FileInputStream(zip))).use { zis ->
            var entry: ZipEntry? = zis.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    val name = entry.name
                    when {
                        name.startsWith("databases/") -> {
                            val dbName = name.removePrefix("databases/")
                            if (dbName.isNotBlank()) {
                                val dbFile = context.getDatabasePath(dbName)
                                writeEntry(zis, dbFile)
                            }
                        }

                        name.startsWith("files/") -> {
                            val rel = name.removePrefix("files/")
                            if (rel.isNotBlank()) {
                                val fileTarget = File(context.filesDir, rel)
                                writeEntry(zis, fileTarget)
                            }
                        }
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }

    private fun writeEntry(zis: ZipInputStream, dest: File) {
        dest.parentFile?.mkdirs()
        val tmp = File(dest.parentFile, dest.name + ".tmp")
        BufferedOutputStream(FileOutputStream(tmp)).use { out ->
            zis.copyTo(out)
        }
        if (dest.exists()) dest.delete()
        if (!tmp.renameTo(dest)) {
            tmp.copyTo(dest, overwrite = true)
            tmp.delete()
        }
    }

    private fun putFile(zos: ZipOutputStream, file: File, zipPath: String) {
        if (!file.exists()) return
        FileInputStream(file).use { fis ->
            zos.putNextEntry(ZipEntry(zipPath))
            fis.copyTo(zos)
            zos.closeEntry()
        }
    }

    // ---------- 유틸 ----------

    /** Room의 SupportSQLiteDatabase를 넘겨 WAL 체크포인트를 수행하는 헬퍼(선택). */
    fun checkpointWAL(db: SupportSQLiteDatabase) {
        runCatching {
            db.query("PRAGMA wal_checkpoint(TRUNCATE);", emptyArray()).close()
        }
    }
}
