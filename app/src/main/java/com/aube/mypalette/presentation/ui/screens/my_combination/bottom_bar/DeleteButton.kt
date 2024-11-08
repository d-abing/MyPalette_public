package com.aube.mypalette.presentation.ui.screens.my_combination.bottom_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aube.mypalette.R

@Composable
fun DeleteButton(onDeleteButtonClick: () -> Unit) {
    IconButton(
        onClick = { onDeleteButtonClick() },
    ) {
        Icon(Icons.Filled.Delete, contentDescription = stringResource(id = R.string.delete))
    }
}