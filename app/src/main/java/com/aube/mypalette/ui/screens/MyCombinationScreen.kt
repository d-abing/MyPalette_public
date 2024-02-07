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
    var selectedId: Int by remember { mutableStateOf(0) }
    var has_modified: Boolean by remember { mutableStateOf(false) }

    // Room 데이터베이스에 초기 데이터 추가
    LaunchedEffect(combinationList) {
        if (combinationList.isEmpty()) {
            // 초기 데이터 추가
            combinationViewModel.insert(CombinationEntity(colors = listOf(Color.Red.toArgb(), Color.Yellow.toArgb(), Color.Green.toArgb(), Color.Blue.toArgb(), Color.Black.toArgb(), Color.White.toArgb(), Color.Cyan.toArgb(), Color.Magenta.toArgb(), Color.Gray.toArgb())))
            combinationViewModel.insert(CombinationEntity(colors = listOf(Color.Red.toArgb(), Color.Yellow.toArgb(), Color.Green.toArgb())))
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
                    IconButton(onClick = { has_modified = false }) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                    }


                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Filled.Edit, contentDescription = null)
                    }


                    IconButton(onClick = {
                        combinationViewModel.delete(selectedId)
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
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
            MyCombinationList(combinationList) {
                selectedId = it
            }
        }
    }
}

@Composable
fun MyCombinationList(combinationList: List<CombinationEntity>, callback: (Int) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        items(combinationList) { combinationItem ->
            ListCombinationItem(combinationItem, callback)
        }
    }
}

@Composable
fun ListCombinationItem(combinationItem: CombinationEntity, callback: (Int) -> Unit) {
    var selectToggle: Boolean by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .clickable {
                selectToggle = true
                callback(combinationItem.id)
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        combinationItem.colors.forEach { colorItem ->
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
