package com.aube.mypalette.presentation.ui.screens.register_color.content

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.palette.graphics.Palette
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.screens.register_color.resetSimilarColorResult
import com.aube.mypalette.presentation.viewmodel.ColorViewModel
import com.aube.mypalette.utils.calculateColorDistance
import com.aube.mypalette.utils.getBitmapFromUri

@Composable
fun ColorPaletteRow(
    context: Context,
    colorPalette: List<MutableState<Color>>,
    selectedImage: Uri?,
    colorViewModel: ColorViewModel,
    lifecycleOwner: LifecycleOwner,
    similarColorResult: MutableState<Pair<Color?, Double?>>,
    onColorSelected: (Color) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        selectedImage?.let {
            val bitmap = context.getBitmapFromUri(it)
            Palette.from(bitmap).generate { palette ->
                palette?.let {
                    colorPalette[0].value = Color(palette.dominantSwatch?.rgb ?: 0)
                    colorPalette[1].value = Color(palette.darkMutedSwatch?.rgb ?: 0)
                    colorPalette[2].value = Color(palette.darkVibrantSwatch?.rgb ?: 0)
                    colorPalette[3].value = Color(palette.lightMutedSwatch?.rgb ?: 0)
                    colorPalette[4].value = Color(palette.lightVibrantSwatch?.rgb ?: 0)
                    colorPalette[5].value = Color(palette.mutedSwatch?.rgb ?: 0)
                    colorPalette[6].value = Color(palette.vibrantSwatch?.rgb ?: 0)
                }
            }
        }

        colorPalette.forEach { color ->
            val boxColor = if (color.value.alpha != 0.0f) color.value else Color.White
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp))
                    .background(boxColor)
                    .clickable {
                        if (selectedImage != null && color.value.alpha != 0.0f) {
                            resetSimilarColorResult(similarColorResult)
                            onColorSelected(color.value)
                            colorViewModel.allColors.observe(lifecycleOwner) { colors ->
                                var closestColor: Color? = null
                                var minDistance: Double? = null

                                colors.forEach { colorEntity ->
                                    val databaseColor = Color(colorEntity.color)
                                    val distance =
                                        calculateColorDistance(color.value, databaseColor)

                                    if (minDistance == null || distance < minDistance!!) {
                                        minDistance = distance
                                        closestColor = databaseColor
                                    }
                                }

                                similarColorResult.value = Pair(closestColor, minDistance)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (color.value.alpha == 0.0f) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.no),
                    )
                }
            }
        }
    }
}