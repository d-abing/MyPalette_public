package com.aube.mypalette.presentation.ui.screens.my_palette

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aube.mypalette.R
import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.data.model.ImageEntity
import com.aube.mypalette.presentation.ui.component.MPDialog
import com.aube.mypalette.presentation.ui.screens.my_palette.content.ColorGrid
import com.aube.mypalette.presentation.ui.screens.my_palette.content.ColorList
import com.aube.mypalette.presentation.ui.screens.my_palette.top_app_bar.MyPaletteTopAppBar
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.viewmodel.ColorViewModel
import com.aube.mypalette.presentation.viewmodel.ImageViewModel
import com.aube.mypalette.utils.copyToClipboard

@Composable
fun MyPaletteScreen(
    colorViewModel: ColorViewModel,
    imageViewModel: ImageViewModel,
) {
    val colorList by colorViewModel.allColors.observeAsState(emptyList())

    var selectedColor by remember { mutableStateOf<ColorEntity?>(null) }
    var selectedImage by remember { mutableStateOf<ImageEntity?>(null) }

    var isListType by remember { mutableStateOf(false) }
    var isLongClicking by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MyPaletteTopAppBar {
                isListType = it
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            if (!isListType) {
                ColorGrid(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(Paddings.medium),
                    colorList = colorList,
                    onColorSelected = {
                        selectedColor = it
                        isLongClicking = true
                    },
                    onTextClick = {
                        context.copyToClipboard(it, coroutineScope, snackbarHostState)
                    }
                )
            } else {
                ColorList(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(Paddings.large),
                    colorList = colorList,
                    imageViewModel = imageViewModel,
                    onColorSelected = {
                        selectedColor = it
                        isLongClicking = true
                    },
                    onImageSelected = {
                        selectedImage = it
                        isLongClicking = true
                    },
                )
            }

            if (isLongClicking) {
                if (selectedColor != null) {
                    MPDialog(
                        bodyText = stringResource(id = R.string.delete_message),
                        onDeleteClick = {
                            selectedColor?.let {
                                colorViewModel.delete(it)
                                isLongClicking = false
                                selectedColor = null
                            }
                        },
                        onCancelClick = {
                            isLongClicking = false
                            selectedColor = null
                        }
                    )
                } else if (selectedImage != null) {
                    MPDialog(
                        bodyText = stringResource(id = R.string.delete_image),
                        onDeleteClick = {
                            selectedImage?.let {
                                imageViewModel.delete(it)
                                isLongClicking = false
                                selectedImage = null
                            }
                        },
                        onCancelClick = {
                            isLongClicking = false
                            selectedImage = null
                        }
                    )
                }
            }
        }
    }
}
