package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.aube.mypalette.database.CombinationEntity
import com.aube.mypalette.viewmodel.CombinationViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCombinationScreen(
    combinationViewModel: CombinationViewModel
) {
    val combinationList by combinationViewModel.allCombinations.observeAsState(emptyList())
    var selectedList by remember { mutableStateOf(ArrayList<Int>()) }
    var has_modified: Boolean by remember { mutableStateOf(false) }

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
                    if (has_modified) {
                        IconButton(onClick = { has_modified = false }) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    }
                    if (selectedList.size == 1) {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                        }
                    }
                    if (selectedList.size >= 1) {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(Icons.Filled.Delete, contentDescription = null)
                        }
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
                selectedList, combinationList
            )
        }
    }
}

@Composable
fun MyCombinationList(selectedList: ArrayList<Int>,combinationList: List<CombinationEntity>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        items(combinationList) { combinationItem ->
            ListCombinationItem(selectedList, combinationItem)
        }
    }
}

@Composable
fun ListCombinationItem(selectedList: ArrayList<Int>,combinationItem: CombinationEntity) {
    var selectToggle: Boolean by remember { mutableStateOf(false) }

    if (!selectToggle) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    selectToggle = true
                    selectedList.add(combinationItem.id)
                    Log.d("test다", selectedList.size.toString())
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(combinationItem.colors) { colorItem ->
                ColorItem(colorItem)
            }
        }
    } else{
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray, RoundedCornerShape(8.dp))
                .clickable {
                    selectToggle = false
                    selectedList.remove(combinationItem.id)
                    Log.d("test다", selectedList.size.toString())
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(combinationItem.colors) { colorItem ->
                ColorItem(colorItem)
            }
        }
    }
}

@Composable
fun ColorItem(color: Int) {
    if (color in -100..0) {
        Box(
            modifier = Modifier
                .size(69.8.dp)
                .border(0.1.dp, Color.Gray)
                .background(Color(color))
        )
    } else {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(Color(color))
        )
    }
}
