package com.aube.mypalette.presentation.ui.screens.my_palette.top_app_bar

import android.content.Intent
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.screens.setting.SettingActivity
import com.aube.mypalette.presentation.ui.theme.MyPaletteTheme
import com.aube.mypalette.presentation.ui.theme.Sizes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPaletteTopAppBar(onViewTypeChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    
    CenterAlignedTopAppBar(
        modifier = Modifier.heightIn(max = Sizes.topBarMaxHeight),
        title = { Text(stringResource(id = R.string.myPalette)) },
        navigationIcon = {
            IconButton(onClick = {
                Intent(context, SettingActivity::class.java).apply {
                    context.startActivity(this)
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(id = R.string.setting)
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    onViewTypeChange(false)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_border_all_24),
                    contentDescription = stringResource(id = R.string.gallery_type)
                )
            }
            IconButton(
                onClick = {
                    onViewTypeChange(true)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_view_list_24),
                    contentDescription = stringResource(id = R.string.list_type)
                )
            }
        }
    )
}

@Preview
@Composable
private fun MyPaletteTopAppBarPreview() {
    val onViewTypeChange: (Boolean) -> Unit = {}
    MyPaletteTheme {
        MyPaletteTopAppBar(onViewTypeChange)
    }
}