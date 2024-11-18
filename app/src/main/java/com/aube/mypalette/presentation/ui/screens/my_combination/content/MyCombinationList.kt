package com.aube.mypalette.presentation.ui.screens.my_combination.content

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aube.mypalette.presentation.model.Combination
import com.aube.mypalette.presentation.ui.theme.MyPaletteTheme
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.ui.theme.Sizes

@Composable
fun MyCombinationList(
    combinationList: List<MutableState<Combination>>,
    addId: (Int) -> Unit,
    removeId: (Int) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Paddings.large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(
            items = combinationList,
            key = { combinationItem -> combinationItem.value.id }
        ) { combinationItem ->
            CombinationItem(
                combinationItem = combinationItem,
                addId = addId,
                removeId = removeId
            )
        }
    }
}

@Composable
fun CombinationItem(
    combinationItem: MutableState<Combination>,
    addId: (Int) -> Unit,
    removeId: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Sizes.combinationItemRowHeight)
            .let { baseModifier ->
                if (!combinationItem.value.isSelected) {
                    baseModifier.border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                } else {
                    baseModifier.background(Color.LightGray, RoundedCornerShape(8.dp))
                }
            }
            .clickable {
                combinationItem.value = combinationItem.value.copy(
                    isSelected = !combinationItem.value.isSelected
                )

                if (!combinationItem.value.isSelected) {
                    removeId(combinationItem.value.id)
                } else {
                    addId(combinationItem.value.id)
                }
            }
            .padding(Paddings.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        combinationItem.value.colors.forEach { colorItem ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color(colorItem))
            ) {}
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun MyCombinationListPreview() {
    MyPaletteTheme {
        MyCombinationList(
            combinationList = listOf(
                mutableStateOf(
                    Combination(
                        id = 0,
                        colors = listOf(
                            -8890344,
                            -4165512,
                            -16220080,
                            -16213968,
                            -1525656,
                        ),
                        isSelected = false
                    )
                ),
                mutableStateOf(
                    Combination(
                        id = 1,
                        colors = listOf(
                            -1525656,
                            -16220020,
                            -1621968,
                            -1556,
                        ),
                        isSelected = false
                    )
                )
            ),
            addId = {},
            removeId = {}
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun CombinationItemPreview() {
    MyPaletteTheme {
        CombinationItem(
            combinationItem = mutableStateOf(
                Combination(
                    id = 0,
                    colors = listOf(
                        -8890344,
                        -4165512,
                        -16220080,
                        -16213968,
                        -1525656,
                    ),
                    isSelected = false
                )
            ),
            addId = {},
            removeId = {}
        )
    }
}