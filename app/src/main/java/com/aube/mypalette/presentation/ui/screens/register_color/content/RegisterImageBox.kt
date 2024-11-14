package com.aube.mypalette.presentation.ui.screens.register_color.content

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aube.mypalette.R
import com.aube.mypalette.utils.getOriginalBitmapFromUri

@Composable
fun ImageBox(
    selectedImage: Uri?,
    context: Context,
    onColorPicked: (Color) -> Unit,
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) } // 터치 좌표
    var tapOffset by remember { mutableStateOf(Offset.Zero) }

    if (selectedImage != null) {
        bitmap = context.getOriginalBitmapFromUri(selectedImage)

        Image(
            painter = rememberAsyncImagePainter(model = selectedImage),
            contentDescription = stringResource(id = R.string.selected_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        tapOffset = offset
                        // 이미지 영역에서의 위치를 비트맵 좌표로 변환
                        val bitmapColor = getBitmapColorFromTap(bitmap, tapOffset, size)
                        bitmapColor?.let { onColorPicked(it) }
                    }
                }

        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray, RoundedCornerShape(16.dp))
        )
    }
}

// 사용자가 터치한 위치의 색상 추출
fun getBitmapColorFromTap(bitmap: Bitmap?, tapOffset: Offset, imageSize: IntSize): Color? {
    if (bitmap == null) return null

    // 이미지의 표시된 사이즈와 비트맵의 사이즈 비율 계산
    val scaleX = bitmap.width.toFloat() / imageSize.width
    val scaleY = bitmap.height.toFloat() / imageSize.height

    // 터치 좌표를 비트맵 좌표로 변환
    val x = (tapOffset.x * scaleX).toInt()
    val y = (tapOffset.y * scaleY).toInt()

    return if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
        Color(bitmap.getPixel(x, y))
    } else {
        null
    }
}
