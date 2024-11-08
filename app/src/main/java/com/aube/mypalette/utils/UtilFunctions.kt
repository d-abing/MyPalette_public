package com.aube.mypalette.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.aube.mypalette.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.sqrt

fun calculateColorDistance(color1: Color, color2: Color): Double {
    val r1 = color1.red
    val g1 = color1.green
    val b1 = color1.blue

    val r2 = color2.red
    val g2 = color2.green
    val b2 = color2.blue

    val deltaR = r1 - r2
    val deltaG = g1 - g2
    val deltaB = b1 - b2

    return sqrt((deltaR * deltaR + deltaG * deltaG + deltaB * deltaB).toDouble())
}

fun isColorBright(color: Int): Boolean {
    val red = android.graphics.Color.red(color)
    val green = android.graphics.Color.green(color)
    val blue = android.graphics.Color.blue(color)

    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
    return luminance > 0.5
}

fun colorToHexString(color: Int): String {
    return String.format("#%06X", color and 0xFFFFFF)
}

fun Context.copyToClipboard(
    text: String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(this.getString(R.string.label), text)
    clipboard.setPrimaryClip(clip)

    showSnackBar(scope, snackbarHostState, this.getString(R.string.copy_message))
}

fun showSnackBar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String,
    actionLabel: String? = null,
    action: () -> Unit = {},
) {
    scope.launch {
        val result = snackbarHostState
            .showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Short
            )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                action()
            }

            SnackbarResult.Dismissed -> {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }
}

@Composable
fun GetCurrentScreenWidth(): Int {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    return screenWidth.value.toInt() // 화면 너비를 픽셀로 반환
}