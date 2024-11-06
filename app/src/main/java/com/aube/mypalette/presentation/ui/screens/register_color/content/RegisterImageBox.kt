package com.aube.mypalette.presentation.ui.screens.register_color.content

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aube.mypalette.R

@Composable
fun ImageBox(
    selectedImage: Uri?,
) {
    return if (selectedImage != null) {
        Image(
            painter = rememberAsyncImagePainter(
                model = selectedImage
            ),
            contentDescription = stringResource(id = R.string.selected_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray, RoundedCornerShape(16.dp))
        )
    }
}