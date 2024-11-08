package com.aube.mypalette.presentation.ui.screens.my_combination.bottom_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import com.aube.mypalette.R

@Composable
fun AddFloating(
    isAdding: Boolean,
    isModifying: Boolean,
    newCombination: SnapshotStateList<Int>,
    onAddButtonClick: () -> Unit,
    onCompleteButtonClick: () -> Unit,
    onCloseButtonClick: () -> Unit,
) {
    if (!isAdding && !isModifying) {
        FloatingActionButton(
            onClick = { onAddButtonClick() },
            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
        ) {
            Icon(Icons.Filled.Add, stringResource(id = R.string.add))
        }
    } else {
        if (newCombination.isNotEmpty()) {
            FloatingActionButton(
                onClick = { onCompleteButtonClick() },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.Check, contentDescription = stringResource(id = R.string.save))
            }
        } else {
            FloatingActionButton(
                onClick = { onCloseButtonClick() },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(id = R.string.close))
            }
        }
    }
}