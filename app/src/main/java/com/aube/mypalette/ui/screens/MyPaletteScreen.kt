package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import android.graphics.ColorSpace
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aube.mypalette.R
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.viewmodel.ColorViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPaletteScreen(
    colorViewModel: ColorViewModel
) {
    // ColorViewModel을 통해 LiveData를 observe하여 상태 감지
    val colorList by colorViewModel.allColors.observeAsState(emptyList())

    // Room 데이터베이스에 초기 데이터 추가
    LaunchedEffect(colorList) {
        if (colorList.isEmpty()) {
            // 초기 데이터 추가
            colorViewModel.insert(ColorEntity(color = android.graphics.Color.rgb(144, 82, 58))) // 90523A
            colorViewModel.insert(ColorEntity(color = android.graphics.Color.rgb(12, 168, 107))) // 0CA86B
            colorViewModel.insert(ColorEntity(color = android.graphics.Color.rgb(149, 167, 190))) // 95a7be
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Palette") }
            )
        }
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 20.dp, end = 20.dp)
                .background(Color(100, 100, 100))
                .fillMaxWidth()
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(colorList) { colorItem ->
                ColorItem(colorItem)
            }
        }
    }
}

@Composable
fun ColorItem(colorItem: ColorEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // 최대 너비를 채우도록 수정
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(colorItem.color))
        )

        LazyRow(
            modifier = Modifier
                .padding(10.dp)
                .background(Color(200, 200, 200))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.example1),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.example2),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.example3),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            // Add more items as needed
        }
    }
}