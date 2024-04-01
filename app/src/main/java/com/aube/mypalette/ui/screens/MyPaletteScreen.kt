package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.aube.mypalette.R
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.database.ImageEntity
import com.aube.mypalette.utils.GetCurrentScreenWidth
import com.aube.mypalette.viewmodel.ColorViewModel
import com.aube.mypalette.viewmodel.ImageViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPaletteScreen(
    colorViewModel: ColorViewModel,
    imageViewModel: ImageViewModel
) {
    val colorList by colorViewModel.allColors.observeAsState(emptyList())
    var listToggle: Boolean by remember { mutableStateOf(false) }
    var selectedColor: ColorEntity? by remember { mutableStateOf(null) }
    var isDragging by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.myPalette)) },
                actions = {
                    IconButton(onClick = {
                        listToggle = false
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_border_all_24),
                            contentDescription = stringResource(id = R.string.gallery)
                        )
                    }
                    IconButton(onClick = {
                        listToggle = true
                        isDragging = false}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_view_list_24),
                            contentDescription = stringResource(id = R.string.list)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(innerPadding)
                .padding(top = 10.dp, start = 20.dp, bottom = 10.dp, end = 20.dp)
                .fillMaxSize()
                .let { baseModifier ->
                    if (isDragging) {
                        baseModifier.clickable { isDragging = false }
                    } else {
                        baseModifier
                    }
                }
            ,
        ) {
            if (!listToggle) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 70.dp),
                ) {
                    items(colorList) { colorItem ->
                        GalleryColorItem(colorItem) {
                            selectedColor = it
                            isDragging = true
                        }
                    }
                }

            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 168.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                    ) {
                    items(colorList) { colorItem ->
                        ListColorItem(colorItem, imageViewModel)
                    }
                }
            }
        }


        if (isDragging) {
            DeleteButton(innerPadding, selectedColor, colorViewModel,
                {
                    isDragging = it
                }, {
                    selectedColor = null
                })
        }
    }
}

@Composable
fun ListColorItem(colorItem: ColorEntity, imageViewModel: ImageViewModel) {
    val imageList by imageViewModel.getImagesForColor(colorItem.id).observeAsState(emptyList())

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .size(if (colorItem.color == 0) 79.8.dp else 80.dp)
                .let { baseModifier ->
                    if (colorItem.color == 0) {
                        baseModifier.border(0.1.dp, Color.Gray)
                    } else {
                        baseModifier
                    }
                }
                .background(Color(colorItem.color))
        )

        LazyRow(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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
        ImageRequest.Builder(LocalContext.current).data(data = imageItem.imageBytes).apply(block = fun ImageRequest.Builder.() {
            crossfade(true)
        }).build()
    )

    Image(
        painter = imagePainter,
        contentDescription = stringResource(id = R.string.image),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun GalleryColorItem(colorItem: ColorEntity, onItemSelected: (ColorEntity) -> Unit) {
    Box(
        modifier = Modifier
            .size(if (colorItem.color == 0) 79.8.dp else 80.dp)
            .let { boxModifier ->
                if (colorItem.color == 0) {
                    boxModifier.border(0.1.dp, Color.Gray)
                } else {
                    boxModifier
                }
            }
            .background(Color(colorItem.color))
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onItemSelected(colorItem)
                    }
                )
            }
    )
}

@Composable
fun DeleteButton(innerPadding: PaddingValues, selectedColor: ColorEntity?, colorViewModel: ColorViewModel, updateDragState: (Boolean) -> Unit, updateSelectedColor: () -> Unit) {
    val width = GetCurrentScreenWidth()

    Box(
        modifier = Modifier
            .padding(innerPadding)
            .padding(top = 500.dp, start = (width / 2 - 40).dp)
            .size(80.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(50))
            .background(Color(selectedColor!!.color), RoundedCornerShape(50))
            .clickable {
                updateDragState(false)
                colorViewModel.delete(selectedColor.id)
                updateSelectedColor()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            modifier = Modifier
                .size(40.dp),
            tint = Color.Gray,
            contentDescription = stringResource(id = R.string.delete),
        )
    }
}
