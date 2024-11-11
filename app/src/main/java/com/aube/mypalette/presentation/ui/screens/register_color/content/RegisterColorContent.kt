package com.aube.mypalette.presentation.ui.screens.register_color.content

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LifecycleOwner
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.viewmodel.ColorViewModel

@Composable
fun RegisterColorContent(
    innerPadding: PaddingValues,
    selectedColor: Color?,
    colorPalette: List<MutableState<Color>>,
    selectedImage: Uri?,
    context: Context,
    onColorPicked: (Color) -> Unit,
    colorViewModel: ColorViewModel,
    lifecycleOwner: LifecycleOwner,
    similarColorResult: MutableState<Pair<Color?, Double?>>,
    onColorSelected: (Color) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .background(selectedColor ?: Color.White)
            .padding(Paddings.large)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(bottom = Paddings.medium),
            contentAlignment = Alignment.Center
        ) {
            ImageBox(selectedImage, context) {
                onColorPicked(it)
            }
        }

        ColorPaletteRow(
            context = context,
            colorPalette = colorPalette,
            selectedImage = selectedImage,
            colorViewModel = colorViewModel,
            lifecycleOwner = lifecycleOwner,
            similarColorResult = similarColorResult,
            onColorSelected = onColorSelected
        )

        SimilarityColor(
            count = colorPalette.count { it.value.alpha != 0.0f },
            selectedImage = selectedImage,
            similarColorResult = similarColorResult
        )

        ImportingButton(
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick
        )
    }
}

