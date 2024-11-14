package com.aube.mypalette.presentation.ui.screens.register_color.content

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.ui.theme.Sizes

const val STANDARD_FOR_SIMILARITY = 85

@Composable
fun SimilarityColor(
    count: Int,
    selectedImage: Uri?,
    similarColorResult: MutableState<Pair<Color?, Double?>>,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(Sizes.similarityColorCardHeight)
            .padding(bottom = Paddings.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (similarColor, distance) = similarColorResult.value
            val scrollState = rememberScrollState()

            when {
                similarColor != null && distance != null -> {
                    val similarityPercentage = ((1 - distance) * 100).toInt()
                    if (similarityPercentage >= STANDARD_FOR_SIMILARITY) {
                        Box(
                            modifier = Modifier
                                .padding(Paddings.large)
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp))
                                .background(Color(similarColor.toArgb()))
                        )
                        Text(
                            text = stringResource(
                                id = R.string.yes_similarity_message,
                                similarityPercentage
                            ),
                            modifier = Modifier
                                .padding(vertical = Paddings.small, horizontal = Paddings.medium)
                                .verticalScroll(scrollState)
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.no_similarity_message),
                            modifier = Modifier
                                .padding(vertical = Paddings.small, horizontal = Paddings.medium)
                                .verticalScroll(scrollState)
                        )
                    }
                }

                selectedImage == null -> {
                    Text(
                        text = stringResource(id = R.string.comparison_message),
                        modifier = Modifier
                            .padding(vertical = Paddings.small, horizontal = Paddings.medium)
                            .verticalScroll(scrollState)
                    )
                }

                else -> {
                    Text(
                        text = stringResource(id = R.string.extracted_colors_message, count),
                        modifier = Modifier
                            .padding(vertical = Paddings.small, horizontal = Paddings.medium)
                            .verticalScroll(scrollState)
                    )
                }
            }
        }
    }
}