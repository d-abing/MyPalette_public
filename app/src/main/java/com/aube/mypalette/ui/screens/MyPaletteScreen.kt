package com.aube.mypalette.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aube.mypalette.model.ColorItem
import com.aube.mypalette.viewmodel.ColorViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyPaletteScreen(
    colorViewModel: ColorViewModel
) {
    val colorList = colorViewModel.getAllColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Palette",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            state = rememberLazyListState()
        ) {
            items(colorList) { colorItem ->
                ColorItemCard(colorItem = colorItem)
            }
        }
    }
}

@Composable
fun ColorItemCard(colorItem: ColorItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(8.dp)
            .background(Color(colorItem.color))
            .clickable {
                // Color item 클릭 시의 동작 정의
            }
    ) {
        // Color item의 내용을 여기에 추가
    }
}