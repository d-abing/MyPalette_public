package com.aube.mypalette.presentation.ui.screens.my_palette.top_app_bar

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.aube.mypalette.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPaletteTopAppBar(onViewTypeChange: (Boolean) -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(id = R.string.myPalette)) },
        actions = {
            IconButton(
                onClick = {
                    onViewTypeChange(false)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_border_all_24),
                    contentDescription = stringResource(id = R.string.gallery)
                )
            }
            IconButton(
                onClick = {
                    onViewTypeChange(true)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_view_list_24),
                    contentDescription = stringResource(id = R.string.list)
                )
            }
        }
    )
}