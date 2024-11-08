package com.aube.mypalette.presentation.ui.screens.my_combination.bottom_bar

import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview
import com.aube.mypalette.presentation.ui.theme.MyPaletteTheme

@Composable
fun MyCombinationBottomAppBar(
    isAdding: Boolean,
    isModifying: Boolean,
    newCombination: SnapshotStateList<Int>,
    onModifyButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onAddButtonClick: () -> Unit,
    onCompleteButtonClick: () -> Unit,
    onCloseButtonClick: () -> Unit,
) {

    BottomAppBar(
        actions = {
            if (!isAdding && !isModifying) {
                // 수정
                ModifyButton(onModifyButtonClick = onModifyButtonClick)

                // 삭제
                DeleteButton(onDeleteButtonClick = onDeleteButtonClick)
            }
        },

        // 추가
        floatingActionButton = {
            AddFloating(
                isAdding = isAdding,
                isModifying = isModifying,
                newCombination = newCombination,
                onAddButtonClick = { onAddButtonClick() },
                onCompleteButtonClick = { onCompleteButtonClick() },
                onCloseButtonClick = { onCloseButtonClick() }
            )
        }
    )
}

@Preview
@Composable
private fun MyCombinationBottomAppBarPreview() {
    MyPaletteTheme {
        MyCombinationBottomAppBar(
            isAdding = false,
            isModifying = false,
            newCombination = SnapshotStateList(),
            onModifyButtonClick = {},
            onDeleteButtonClick = {},
            onAddButtonClick = {},
            onCompleteButtonClick = {},
            onCloseButtonClick = {}
        )
    }
}