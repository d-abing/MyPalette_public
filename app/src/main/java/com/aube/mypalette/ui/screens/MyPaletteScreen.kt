package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.aube.mypalette.R
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.database.ImageEntity
import com.aube.mypalette.viewmodel.ColorViewModel
import com.aube.mypalette.viewmodel.ImageViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPaletteScreen(
    colorViewModel: ColorViewModel,
    imageViewModel: ImageViewModel
) {
    // ColorViewModel을 통해 LiveData를 observe하여 상태 감지
    val colorList by colorViewModel.allColors.observeAsState(emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Palette") },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "List"
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.AccountBox,
                            contentDescription = "Palette"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .background(Color.White)
                .padding(innerPadding)
                .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 10.dp)
                .fillMaxSize()
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(colorList) { colorItem ->
                ColorItem(colorItem, imageViewModel)
            }
        }
    }
}

@Composable
fun ColorItem(colorItem: ColorEntity, imageViewModel: ImageViewModel) {
    val imageList by imageViewModel.getImagesForColor(colorItem.id).observeAsState(emptyList())

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(80.dp)
                .background(Color(colorItem.color))
        )

        LazyRow(
            modifier = Modifier
                .padding(start = 8.dp, top = 10.dp, bottom = 10.dp)
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
        painter = imagePainter, // imageItem을 어떻게 불러올지
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}