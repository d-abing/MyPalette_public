package com.aube.mypalette.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.viewmodel.ColorViewModel


@Composable
fun AddCombinationScreen(newCombination: List<Int>, colorViewModel: ColorViewModel, content: () -> Boolean, addColor: (Int) -> Unit) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NewCombination(newCombination)
        Spacer(Modifier.height(10.dp))
        MyPaletteColor(colorViewModel, addColor)
    }
}

@Composable
fun NewCombination(newCombination: List<Int>) {
    val colors = remember { newCombination }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        colors.forEach { colorItem ->
            if (colorItem in -100..0) {
                Column(
                    modifier = Modifier
                        .height(99.6.dp)
                        .weight(1f)
                        .border(0.1.dp, Color.Gray)
                        .background(Color(colorItem))
                ){}
            } else {
                Column(
                    modifier = Modifier
                        .height(100.dp)
                        .weight(1f)
                        .background(Color(colorItem))
                ){}
            }
        }
    }
}

@Composable
fun MyPaletteColor(colorViewModel: ColorViewModel, addColor: (Int) -> Unit) {
    val colorList by colorViewModel.allColors.observeAsState(emptyList())

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 60.dp),
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        items(colorList) { colorItem ->
            ClickablePaletteColorItem(colorItem, addColor)
        }
    }
}

@Composable
fun ClickablePaletteColorItem(colorItem: ColorEntity, addColor: (Int) -> Unit) {
    if (colorItem.color == 0) {
        Box(
            modifier = Modifier
                .size(59.8.dp)
                .border(0.1.dp, Color.Gray)
                .background(Color(colorItem.color))
                .clickable { addColor(colorItem.color) }
        )
    } else {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(colorItem.color))
                .clickable { addColor(colorItem.color) }
        )
    }
}

