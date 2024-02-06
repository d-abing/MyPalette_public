package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.aube.mypalette.database.ColorListConverter
import com.aube.mypalette.database.CombinationEntity
import com.aube.mypalette.viewmodel.CombinationViewModel
import java.lang.reflect.TypeVariable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCombinationScreen(
    combinationViewModel: CombinationViewModel
) {
    val combinationList by combinationViewModel.allCombinations.observeAsState(emptyList())

    // Room 데이터베이스에 초기 데이터 추가
    LaunchedEffect(combinationList) {
        if (combinationList.isEmpty()) {
            // 초기 데이터 추가
            combinationViewModel.insert(CombinationEntity(colors = listOf(Color.Red.toArgb(), Color.Yellow.toArgb(), Color.Green.toArgb())))
            combinationViewModel.insert(CombinationEntity(colors = listOf(Color.Blue.toArgb(), Color.Black.toArgb(), Color.White.toArgb())))
            combinationViewModel.insert(CombinationEntity(colors = listOf(Color.Cyan.toArgb(), Color.Magenta.toArgb(), Color.Gray.toArgb())))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Combinations") }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Filled.Check, contentDescription = "Localized description")
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* do something */ },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                }
            )
        },
    ) {
        innerPadding ->

        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(innerPadding)
                .padding(top = 10.dp, start = 20.dp, bottom = 10.dp, end = 20.dp)
                .fillMaxSize(),
        ) {
            MyCombinationList(
                combinationList
            )
        }
    }
}

@Composable
fun MyCombinationList(combinationList: List<CombinationEntity>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        items(combinationList) { combinationItem ->
            ListCombinationItem(combinationItem)
        }
    }
}

@Composable
fun ListCombinationItem(combinationItem: CombinationEntity) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .clickable { /* Handle click event if needed */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(combinationItem.colors) { colorItem ->
            ColorItem(colorItem)
        }
    }
}

@Composable
fun ColorItem(color: Int) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(Color(color), RoundedCornerShape(8.dp))
    )
}