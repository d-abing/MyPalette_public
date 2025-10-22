package com.aube.mypalette.presentation.ui.screens.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.viewmodel.SettingsViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes


// --- Public models ------------------------------------------------------------

enum class LanguageOption(val tag: String, val label: String) {
    SYSTEM("", "System"),
    EN("en", "English"),
    AR("ar", "العربية"),
    DE("de", "Deutsch"),
    ES("es", "Español"),
    FR("fr", "Français"),
    HI("hi", "हिन्दी"),
    ID("id", "Bahasa Indonesia"),
    IT("it", "Italiano"),
    JA("ja", "日本語"),
    KO("ko", "한국어"),
    PL("pl", "Polski"),
    PT("pt", "Português"),
    TH("th", "ภาษาไทย"),
    TR("tr", "Türkçe"),
    VI("vi", "Tiếng Việt"),
    ZH("zh", "中文 (简体)");
}

data class DriveState(
    val connected: Boolean = false,
    val accountName: String? = null,
    val lastBackup: Long? = null,
    val busy: Boolean = false,
    val error: String? = null,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    vm: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val driveState by vm.drive.collectAsState()
    val currentLanguage by vm.currentLanguage.collectAsState()

    val signInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(res.data)
        try {
            val account = task.getResult(ApiException::class.java)

            // 스코프가 실제로 붙었는지 확인
            val hasDriveScope = GoogleSignIn.hasPermissions(
                account,
                Scope(DriveScopes.DRIVE_APPDATA)
            )
            if (hasDriveScope) {
                vm.onGoogleSignedIn(account) // ← 여기서 busy=false로 내려주거나 다음 단계 진행
            } else {
                // 스코프 미부여면 다시 signInOptions로 재시도(아래 launchSignIn 호출)
                // Log.e("SignIn", "Missing DRIVE_APPDATA scope; relaunching sign-in...")
            }
        } catch (e: ApiException) {
            // Log.e("SignIn", "GoogleSignIn failed: status=${e.statusCode}, msg=${e.message}", e)
        }
    }

    fun launchSignIn(context: Context) {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
            .build()
        val client = GoogleSignIn.getClient(context, signInOptions)
        signInLauncher.launch(client.signInIntent)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = { Text(getString(context, R.string.setting)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(Paddings.xlarge)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Paddings.xlarge)
        ) {
            // Language Section
            SettingCard(title = stringResource(R.string.text_language)) {
                LanguageSelectorRow(
                    current = currentLanguage,
                    onChange = { vm.changeLanguage(it) }
                )
            }

            // Privacy Policy Section
            SettingCard(title = stringResource(R.string.text_privacy_policy)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://aubecompany.blogspot.com/2024/03/privacy-policy-for-my-palette.html")
                            )
                            context.startActivity(intent)
                        }
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.text_privacy_policy_detail),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Google Drive Section
            SettingCard(title = stringResource(R.string.text_google_drive)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (driveState.connected) {
                        Text(
                            text = stringResource(R.string.text_status_connected) + (driveState.accountName?.let { " ($it)" }
                                ?: ""),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        driveState.lastBackup?.let {
                            val localZone = java.time.ZoneId.systemDefault()
                            val formatter = java.time.format.DateTimeFormatter
                                .ofPattern("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())

                            val text = java.time.Instant.ofEpochMilli(it)
                                .atZone(localZone)
                                .format(formatter)

                            Text(
                                stringResource(R.string.text_last_backup) + text,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(
                                onClick = vm::backupNow,
                                enabled = !driveState.busy
                            ) {
                                if (driveState.busy) CircularProgressIndicator(
                                    modifier = Modifier.size(
                                        16.dp
                                    ), strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(stringResource(R.string.text_back_up_now))
                            }
                            OutlinedButton(
                                onClick = vm::restoreLatest,
                                enabled = !driveState.busy
                            ) { Text(stringResource(R.string.text_restore)) }
                            OutlinedButton(
                                onClick = {
                                    vm.disconnect(context)
                                },
                                enabled = !driveState.busy
                            ) { Text(stringResource(R.string.text_disconnect)) }
                        }
                    } else {
                        Text(
                            stringResource(R.string.text_status_not_connected),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = {
                                launchSignIn(context)
                            },
                            enabled = !driveState.busy
                        ) {
                            if (driveState.busy) CircularProgressIndicator(
                                modifier = Modifier.size(
                                    16.dp
                                ), strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.text_connect_google_drive))
                        }
                    }
                    Text(
                        stringResource(R.string.text_back_up_detail),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        content()
    }
}

@Composable
private fun LanguageSelectorRow(
    current: LanguageOption,
    onChange: (LanguageOption) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(12.dp)
        ) {
            Spacer(Modifier.width(8.dp))
            Text(
                current.label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clip(RoundedCornerShape(12.dp))
        ) {
            LanguageOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
