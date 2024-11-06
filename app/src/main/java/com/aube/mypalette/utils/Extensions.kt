package com.aube.mypalette.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.io.ByteArrayOutputStream

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun Bitmap.toBytes(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 50, stream)
    return stream.toByteArray()
}

fun Context.getBitmapFromUri(uri: Uri): Bitmap {
    val contentResolver: ContentResolver = this.contentResolver
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true // 먼저 이미지 크기만 읽기
    }

    // 이미지 크기 확인을 위해 한 번 디코딩
    contentResolver.openInputStream(uri)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream, null, options)
    }

    // 적절한 inSampleSize 계산 (이미지를 더 작게 로드하여 메모리 절약)
    options.inSampleSize = calculateInSampleSize(options)
    options.inJustDecodeBounds = false // 이제 실제 이미지를 디코딩

    // 회전 각도 계산
    val rotationDegrees = getRotationDegrees(contentResolver, uri)

    // 이미지 디코딩 및 회전 적용
    return contentResolver.openInputStream(uri)?.use { inputStream ->
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        bitmap?.let { rotateBitmap(it, rotationDegrees) } ?: Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        )
    } ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
}

// 회전 각도를 얻기 위한 함수
fun getRotationDegrees(contentResolver: ContentResolver, uri: Uri): Float {
    contentResolver.openInputStream(uri)?.use { inputStream ->
        val exif = ExifInterface(inputStream)
        return when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }
    return 0f
}

// Bitmap을 회전하는 함수
fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Float): Bitmap {
    return if (rotationDegrees != 0f) {
        val matrix = Matrix().apply { postRotate(rotationDegrees) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}

// 이미지의 inSampleSize를 계산하는 함수
fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int = 1080,
    reqHeight: Int = 1080,
): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}