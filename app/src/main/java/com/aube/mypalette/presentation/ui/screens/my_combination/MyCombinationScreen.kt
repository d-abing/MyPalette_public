package com.aube.mypalette.presentation.ui.screens.my_combination

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aube.mypalette.R
import com.aube.mypalette.data.model.CombinationEntity
import com.aube.mypalette.presentation.model.Combination
import com.aube.mypalette.presentation.model.toUiModel
import com.aube.mypalette.presentation.ui.component.AdMobBanner
import com.aube.mypalette.presentation.ui.screens.my_combination.add_combination.AddCombinationScreen
import com.aube.mypalette.presentation.ui.screens.my_combination.bottom_bar.MyCombinationBottomAppBar
import com.aube.mypalette.presentation.ui.screens.my_combination.content.MyCombinationList
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.ui.theme.Sizes
import com.aube.mypalette.presentation.viewmodel.ColorViewModel
import com.aube.mypalette.presentation.viewmodel.CombinationViewModel
import com.aube.mypalette.utils.showSnackBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCombinationScreen(
    navController: NavHostController,
    combinationViewModel: CombinationViewModel,
    colorViewModel: ColorViewModel,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val combinationEntities = combinationViewModel.allCombinations.observeAsState(emptyList())
    val combinationList = remember(combinationEntities.value) {
        combinationEntities.value.map { combinationEntity ->
            mutableStateOf(combinationEntity.toUiModel())
        }
    }

    val selectedIds: MutableList<Int> = remember { mutableListOf() }
    var isModifying by remember { mutableStateOf(false) }
    var isAdding by remember { mutableStateOf(false) }
    var newCombination: SnapshotStateList<Int> by remember { mutableStateOf(SnapshotStateList()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.heightIn(max = Sizes.topBarMaxHeight),
                title = { Text(stringResource(R.string.myCombination)) }
            )
        },
        bottomBar = {
            Column {
                AdMobBanner()
                MyCombinationBottomAppBar(
                    isAdding = isAdding,
                    isModifying = isModifying,
                    newCombination = newCombination,
                    onDeleteButtonClick = {
                        if (selectedIds.isNotEmpty()) {
                            selectedIds.forEach { combinationViewModel.delete(it) }
                            selectedIds.clear()
                        }
                    },
                    onModifyButtonClick = {
                        if (selectedIds.isNotEmpty() && selectedIds.size == 1) {
                            combinationViewModel.getCombination(selectedIds[0])
                            combinationViewModel.combination.observe(lifecycleOwner) { combination ->
                                newCombination = combination.colors.toMutableStateList()
                            }
                            isModifying = true
                            navController.navigate(context.getString(R.string.addCombinationScreen))
                        } else {
                            showSnackBar(
                                scope,
                                snackbarHostState,
                                context.getString(R.string.one_combination_message),
                                context.getString(R.string.ok)
                            ) {
                                snackbarHostState.currentSnackbarData?.dismiss()
                            }
                        }
                    },
                    onAddButtonClick = {
                        isAdding = true
                        navController.navigate("addCombinationScreen")
                    },
                    onCompleteButtonClick = {
                        if (isModifying) { // 수정 중
                            combinationViewModel.insert(
                                CombinationEntity(
                                    id = selectedIds[0],
                                    colors = newCombination
                                )
                            )
                            isModifying = false
                        } else {  // 추가 중
                            combinationViewModel.insert(CombinationEntity(colors = newCombination))
                            isAdding = false
                        }
                        navController.popBackStack(
                            context.getString(R.string.myCombinationScreen),
                            inclusive = false
                        )
                        newCombination = SnapshotStateList()
                        resetSelection(selectedIds, combinationList)
                    },
                    onCloseButtonClick = {
                        navController.popBackStack(
                            context.getString(R.string.myCombinationScreen),
                            inclusive = false
                        )
                        isAdding = false
                        resetSelection(selectedIds, combinationList)
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(innerPadding)
                .padding(Paddings.large)
                .fillMaxSize(),
        ) {
            NavHost(
                navController = navController,
                startDestination = stringResource(id = R.string.myCombinationScreen)
            ) {
                composable(context.getString(R.string.myCombinationScreen)) {
                    MyCombinationList(combinationList,
                        addId = {
                            selectedIds.add(it)
                        },
                        removeId = {
                            selectedIds.remove(it)
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
                        },
                        onBackPressed = {
                            navController.popBackStack(
                                context.getString(R.string.myCombinationScreen),
                                inclusive = false
                            )
                            isAdding = false
                            isModifying = false
                            resetSelection(selectedIds, combinationList)
                        }
                    )
                }
            }
        }
    }
}

private fun resetSelection(
    selectedIds: MutableList<Int>,
    combinationList: List<MutableState<Combination>>,
) {
    selectedIds.forEach {
        val index =
            combinationList.indexOfFirst { combination -> combination.value.id == it }
        combinationList[index].value = combinationList[index].value.copy(
            isSelected = false
        )
    }
    selectedIds.clear()
}