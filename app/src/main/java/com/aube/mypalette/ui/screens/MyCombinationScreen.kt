package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aube.mypalette.database.CombinationEntity
import com.aube.mypalette.utils.observeOnce
import com.aube.mypalette.utils.showSnackBar
import com.aube.mypalette.viewmodel.ColorViewModel
import com.aube.mypalette.viewmodel.CombinationViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCombinationScreen(
    combinationViewModel: CombinationViewModel,
    colorViewModel: ColorViewModel,
    lifecycleOwner: LifecycleOwner,
) {
    val combinationList by combinationViewModel.allCombinations.observeAsState(emptyList())
    val selectedIds: MutableList<Int> = remember { mutableListOf() }
    val navController = rememberNavController()
    var isUpdating by remember { mutableStateOf(false) }
    var isAdding by remember { mutableStateOf(false) }
    var newCombination: SnapshotStateList<Int> by remember { mutableStateOf(SnapshotStateList())}
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Combinations") }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    if (!isAdding && !isUpdating) {
                        // 수정
                        IconButton(onClick = {
                            if (selectedIds.isNotEmpty() && selectedIds.size == 1) {
                                combinationViewModel.getCombination(selectedIds[0])
                                combinationViewModel.combination.observe(lifecycleOwner, Observer { combination ->
                                    newCombination = combination.colors.toMutableStateList()

                                })
                                isUpdating = true
                                navController.navigate("addCombinationScreen")
                                selectedIds.clear()
                            } else {
                                showSnackBar(scope, snackbarHostState, " ☝ 한 개의 조합을 선택해 주세요 ☝ ", "확인"){}
                            }
                        },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "수정")
                        }

                        // 삭제
                        IconButton(
                            onClick = {
                                if (selectedIds.isNotEmpty()) {
                                    for (id in selectedIds) {
                                        combinationViewModel.delete(id)
                                    }
                                    selectedIds.clear()
                                }
                            },
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "삭제")
                        }
                    }
                },

                // 추가
                floatingActionButton = {
                    if (!isAdding && !isUpdating) {
                        FloatingActionButton(
                            onClick = {
                                isAdding = true
                                navController.navigate("addCombinationScreen")
                                selectedIds.clear()
                            },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(Icons.Filled.Add, "추가")
                        }
                    } else {
                        if (newCombination.isNotEmpty()) {
                            FloatingActionButton(
                                onClick = {
                                    if (isUpdating) {
                                        combinationViewModel.combination.observeOnce(lifecycleOwner, Observer { combination ->
                                            combinationViewModel.update(CombinationEntity(id = combination.id, colors = newCombination))
                                        })
                                        isUpdating = false
                                    } else {
                                        combinationViewModel.insert(CombinationEntity(colors = newCombination))
                                        isAdding = false
                                    }
                                    navController.popBackStack("myCombinationScreen", inclusive = false)
                                    newCombination = SnapshotStateList()
                                },
                                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = "저장")
                            }
                        } else {
                            FloatingActionButton(
                                onClick = {
                                    navController.popBackStack("myCombinationScreen", inclusive = false)
                                    isAdding = false
                                },
                                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = "닫기")
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        innerPadding ->

        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(innerPadding)
                .padding(top = 10.dp, start = 20.dp, bottom = 10.dp, end = 20.dp)
                .fillMaxSize(),
        ) {
            NavHost(
                navController = navController,
                startDestination = "myCombinationScreen"
            ) {
                composable("myCombinationScreen") {
                    MyCombinationList(combinationList, {
                        selectedIds.add(it!!)
                    }, {
                        selectedIds.remove(it!!)
                    })
                }
                composable("addCombinationScreen") {
                    AddCombinationScreen(newCombination, colorViewModel,
                        addColor = {
                            newCombination.add(it)
                        },
                        removeColor = {
                          newCombination.remove(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MyCombinationList(combinationList: List<CombinationEntity>, addId: (Int?) -> Unit, removeId: (Int?) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        items(combinationList) { combinationItem ->
            CombinationItem(combinationItem, addId, removeId)
        }
    }
}

@Composable
fun CombinationItem(combinationItem: CombinationEntity, addId: (Int?) -> Unit, removeId: (Int?) -> Unit) {
    // TODO: 선택되는 방식 변경해야 함
    var selectToggle: Boolean by remember { mutableStateOf(false) }

    Row (
        modifier =  if (!selectToggle) {
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .clickable {
                    selectToggle = true
                    addId(combinationItem.id)
                }
                .padding(10.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp))
                .clickable {
                    selectToggle = false
                    removeId(combinationItem.id)
                }
                .padding(10.dp)
               },
        verticalAlignment = Alignment.CenterVertically,
        ) {
        combinationItem.colors.forEach { colorItem ->
            Column(
                modifier =  if (colorItem in -100..0) {
                    Modifier
                        .height(99.6.dp)
                        .weight(1f)
                        .border(0.1.dp, Color.Gray)
                        .background(Color(colorItem))
                } else {
                    Modifier
                        .height(100.dp)
                        .weight(1f)
                        .background(Color(colorItem))
                }
            ){}
        }
    }
}
