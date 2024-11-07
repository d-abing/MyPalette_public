package com.aube.mypalette.presentation.ui.screens.register_color.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.component.MPIconButton
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.ui.theme.Sizes

@Composable
fun ImportingButton(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(Sizes.importingButtonRowHeight)
    ) {
        MPIconButton(R.drawable.baseline_photo_camera_24, R.string.camera) {
            onCameraClick()
        }

        Spacer(Modifier.width(Paddings.medium))

        MPIconButton(R.drawable.baseline_image_24, R.string.gallery) {
            onGalleryClick()
        }
    }
}