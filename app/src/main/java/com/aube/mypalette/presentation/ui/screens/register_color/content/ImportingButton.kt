package com.aube.mypalette.presentation.ui.screens.register_color.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.component.PaletteButton

@Composable
fun ImportingButton(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        PaletteButton(R.drawable.baseline_photo_camera_24, R.string.camera) {
            onCameraClick()
        }

        Spacer(Modifier.width(10.dp))

        PaletteButton(R.drawable.baseline_image_24, R.string.gallery) {
            onGalleryClick()
        }
    }
}