package com.aube.mypalette.presentation.ui.screens.my_palette.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.aube.mypalette.R
import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.data.model.ImageEntity
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.ui.theme.Sizes
import com.aube.mypalette.presentation.viewmodel.ImageViewModel

@Composable
fun ColorList(
    modifier: Modifier,
    colorList: List<ColorEntity>,
    imageViewModel: ImageViewModel,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = Sizes.colorBoxAndImageRowWidth),
        horizontalArrangement = Arrangement.spacedBy(Paddings.xlarge),
        verticalArrangement = Arrangement.spacedBy(Paddings.xlarge),
        modifier = modifier.fillMaxSize()
    ) {
        items(colorList) { colorItem ->
            ListColorItem(colorItem, imageViewModel)
        }
    }
}

@Composable
fun ListColorItem(colorItem: ColorEntity, imageViewModel: ImageViewModel) {
    val imageList by imageViewModel.getImagesForColor(colorItem.id).observeAsState(emptyList())

    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(Sizes.colorCardSize)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(colorItem.color))
        )
        LazyRow(
            modifier = Modifier
                .padding(start = Paddings.medium)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Paddings.xlarge)
        ) {
            items(imageList) { imageItem ->
                ImageItem(imageItem)
            }
        }
    }
}

@Composable
fun ImageItem(imageItem: ImageEntity) {
    val imagePainter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageItem.imageBytes)
            .apply { crossfade(true) }
            .build()
    )

    Image(
        painter = imagePainter,
        contentDescription = stringResource(id = R.string.image),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(Sizes.colorCardSize)
            .clip(RoundedCornerShape(8.dp))
    )
}