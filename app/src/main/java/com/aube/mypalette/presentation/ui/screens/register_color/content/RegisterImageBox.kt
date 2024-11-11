package com.aube.mypalette.presentation.ui.screens.register_color.content

import android.content.Context
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aube.mypalette.R
import com.aube.mypalette.utils.getBitmapFromUri

@Composable
fun ImageBox(
    selectedImage: Uri?,
    context: Context,
    onColorPicked: (Color) -> Unit,
) {
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    return if (selectedImage != null) {
        val bitmap = context.getBitmapFromUri(selectedImage)

        Image(
            painter = rememberAsyncImagePainter(
                model = selectedImage
            ),
            contentDescription = stringResource(id = R.string.selected_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .onGloballyPositioned { layoutCoordinates ->
                    imageSize = layoutCoordinates.size
                }
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        bitmap.let { bmp ->
                            val x = (tapOffset.x * bmp.width / imageSize.width).toInt()
                            val y = (tapOffset.y * bmp.height / imageSize.height).toInt()

                            // x, y 위치의 색상 추출
                            if (x in 0 until bmp.width && y in 0 until bmp.height) {
                                val pixelColor = bmp.getPixel(x, y)
                                onColorPicked(Color(pixelColor))
                            }
                        }
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