package com.aube.mypalette.presentation.ui.screens.my_combination

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aube.mypalette.R
import com.aube.mypalette.data.model.CombinationEntity
import com.aube.mypalette.presentation.viewmodel.ColorViewModel
import com.aube.mypalette.presentation.viewmodel.CombinationViewModel
import com.aube.mypalette.utils.observeOnce
import com.aube.mypalette.utils.showSnackBar
import kotlinx.coroutines.CoroutineScope

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCombinationScreen(
    combinationViewModel: CombinationViewModel,
    colorViewModel: ColorViewModel,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val combinationList by combinationViewModel.allCombinations.observeAsState(emptyList())
    val selectedIds: MutableList<Int> = remember { mutableListOf() }
    val navController = rememberNavController()
    var isUpdating by remember { mutableStateOf(false) }
    var isAdding by remember { mutableStateOf(false) }
    var newCombination: SnapshotStateList<Int> by remember { mutableStateOf(SnapshotStateList()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.myCombination)) }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    if (!isAdding && !isUpdating) {
                        // 수정
                        ModifyIcon(selectedIds, combinationViewModel, lifecycleOwner, navController,
                            context, scope, snackbarHostState, {
                                isUpdating = it
                            }, {
                                newCombination = it
                            })

                        // 삭제
                        DeleteIcon(selectedIds, combinationViewModel)
                    }
                },

                // 추가
                floatingActionButton = {
                    AddFloating(isAdding,
                        isUpdating,
                        navController,
                        selectedIds,
                        newCombination,
                        combinationViewModel,
                        lifecycleOwner,
                        context,
                        {
                            isAdding = it
                        },
                        {
                            isUpdating = it
                        },
                        {
                            newCombination = it
                        })

                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(innerPadding)
                .padding(top = 10.dp, start = 20.dp, bottom = 10.dp, end = 20.dp)
                .fillMaxSize(),
        ) {
            NavHost(
                navController = navController,
                startDestination = stringResource(id = R.string.myCombinationScreen)
            ) {
                composable(context.getString(R.string.myCombinationScreen)) {
                    MyCombinationList(combinationList,
                        addId = {
                            selectedIds.add(it!!)
                        },
                        removeId = {
                            selectedIds.remove(it!!)
                        }
                    )
                }
                composable(context.getString(R.string.addCombinationScreen)) {
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
fun ModifyIcon(
    selectedIds: MutableList<Int>,
    combinationViewModel: CombinationViewModel,
    lifecycleOwner: LifecycleOwner,
    navController: NavController,
    context: Context,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    changeUpdateState: (Boolean) -> Unit,
    updateNewCombination: (SnapshotStateList<Int>) -> Unit,
) {
    IconButton(
        onClick = {
            if (selectedIds.isNotEmpty() && selectedIds.size == 1) {
                combinationViewModel.getCombination(selectedIds[0])
                combinationViewModel.combination.observe(lifecycleOwner, Observer { combination ->
                    updateNewCombination(combination.colors.toMutableStateList())
                })
                changeUpdateState(true)
                navController.navigate(context.getString(R.string.addCombinationScreen))
                selectedIds.clear()
            } else {
                showSnackBar(
                    scope,
                    snackbarHostState,
                    context.getString(R.string.one_combination),
                    context.getString(R.string.yes)
                ) {}
            }
        },
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Icon(Icons.Filled.Edit, contentDescription = stringResource(id = R.string.modify))
    }
}

@Composable
fun DeleteIcon(selectedIds: MutableList<Int>, combinationViewModel: CombinationViewModel) {
    IconButton(
        onClick = {
            if (selectedIds.isNotEmpty()) {
                selectedIds.forEach { combinationViewModel.delete(it) }
                selectedIds.clear()
            }
        },
    ) {
        Icon(Icons.Filled.Delete, contentDescription = stringResource(id = R.string.delete))
    }
}

@Composable
fun AddFloating(
    isAdding: Boolean,
    isUpdating: Boolean,
    navController: NavController,
    selectedIds: MutableList<Int>,
    newCombination: SnapshotStateList<Int>,
    combinationViewModel: CombinationViewModel,
    lifecycleOwner: LifecycleOwner,
    context: Context,
    changeAddState: (Boolean) -> Unit,
    changeUpdateState: (Boolean) -> Unit,
    updateNewCombination: (SnapshotStateList<Int>) -> Unit,
) {
    if (!isAdding && !isUpdating) {
        FloatingActionButton(
            onClick = {
                changeAddState(true)
                navController.navigate("addCombinationScreen")
                selectedIds.clear()
            },
            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
        ) {
            Icon(Icons.Filled.Add, stringResource(id = R.string.add))
        }
    } else {
        if (newCombination.isNotEmpty()) {
            FloatingActionButton(
                onClick = {
                    if (isUpdating) {
                        combinationViewModel.combination.observeOnce(
                            lifecycleOwner,
                            Observer { combination ->
                                combinationViewModel.update(
                                    CombinationEntity(
                                        id = combination.id,
                                        colors = newCombination
                                    )
                                )
                            })
                        changeUpdateState(false)
                    } else {
                        combinationViewModel.insert(CombinationEntity(colors = newCombination))
                        changeAddState(false)
                    }
                    navController.popBackStack(
                        context.getString(R.string.myCombinationScreen),
                        inclusive = false
                    )
                    updateNewCombination(SnapshotStateList())
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.Check, contentDescription = stringResource(id = R.string.save))
            }
        } else {
            FloatingActionButton(
                onClick = {
                    navController.popBackStack(
                        context.getString(R.string.myCombinationScreen),
                        inclusive = false
                    )
                    changeAddState(false)
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(id = R.string.close))
            }
        }
    }
}


@Composable
fun MyCombinationList(
    combinationList: List<CombinationEntity>,
    addId: (Int?) -> Unit,
    removeId: (Int?) -> Unit,
) {
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
fun CombinationItem(
    combinationItem: CombinationEntity,
    addId: (Int?) -> Unit,
    removeId: (Int?) -> Unit,
) {
    var isSelected: Boolean by remember { mutableStateOf(combinationItem.isSelected) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .let { baseModifier ->
                if (!isSelected) {
                    baseModifier.border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                } else {
                    baseModifier.background(Color.LightGray, RoundedCornerShape(8.dp))
                }
            }
            .clickable {
                isSelected = !isSelected
                if (!isSelected) {
                    removeId(combinationItem.id)
                } else {
                    addId(combinationItem.id)
                }
                combinationItem.isSelected = isSelected
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        combinationItem.colors.forEach { colorItem ->
            Column(
                modifier = Modifier
                    .let { baseModifier ->
                        if (colorItem in -100..0) {
                            baseModifier
                                .height(99.6.dp)
                                .border(0.1.dp, Color.Gray)
                                .background(Color(colorItem))
                        } else {
                            baseModifier
                                .height(100.dp)
                        }
                    }
                    .weight(1f)
                    .background(Color(colorItem))
            ) {}
        }
    }
}

