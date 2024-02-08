package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
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
    var listToggle: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Palette") },
                actions = {
                    IconButton(onClick = { listToggle = false }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_border_all_24),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { listToggle = true }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_view_list_24),
                            contentDescription = "List"
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
                .fillMaxSize(),
        ) {
            if (!listToggle) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 70.dp),
                ) {
                    items(colorList) { colorItem ->
                        PaletteColorItem(colorItem)
                    }
                }

            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    items(colorList) { colorItem ->
                        ListColorItem(colorItem, imageViewModel)
                    }
                }
            }
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

        if (colorItem.color == 0) {
            Box(
                modifier = Modifier
                    .size(79.8.dp)
                    .border(0.1.dp, Color.Gray)
                    .background(Color(colorItem.color))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(colorItem.color))
            )
        }

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
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
fun PaletteColorItem(colorItem: ColorEntity) {
    if (colorItem.color == 0) {
        Box(
            modifier = Modifier
                .size(79.8.dp)
                .border(0.1.dp, Color.Gray)
                .background(Color(colorItem.color))
        )
    } else {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(colorItem.color))
        )
    }
}