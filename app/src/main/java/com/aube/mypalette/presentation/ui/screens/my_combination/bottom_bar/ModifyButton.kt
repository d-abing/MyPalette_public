package com.aube.mypalette.presentation.ui.screens.my_combination.bottom_bar

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.theme.Paddings

@Composable
fun ModifyButton(
    onModifyButtonClick: () -> Unit,
) {
    IconButton(
        onClick = { onModifyButtonClick() },
        modifier = Modifier.padding(start = Paddings.medium)
    ) {
        Icon(Icons.Filled.Edit, contentDescription = stringResource(id = R.string.modify))
    }
}