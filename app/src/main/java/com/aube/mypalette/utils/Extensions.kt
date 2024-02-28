package com.aube.mypalette.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.io.ByteArrayOutputStream
import java.io.InputStream

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
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun Context.getBitmapFromUri(uri: Uri): Bitmap? {
    val contentResolver: ContentResolver = this.contentResolver
    try {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}