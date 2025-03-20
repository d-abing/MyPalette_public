package com.aube.mypalette.presentation.ui.screens.register_color.top_app_bar

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.aube.mypalette.PALETTE_STATE
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.screens.setting.SettingActivity
import com.aube.mypalette.presentation.ui.theme.Sizes
import com.aube.mypalette.presentation.viewmodel.AdViewModel
import com.aube.mypalette.presentation.viewmodel.ImageViewModel
import com.aube.mypalette.utils.getBitmapFromUri
import com.aube.mypalette.utils.showSnackBar
import com.aube.mypalette.utils.toBytes
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterColorTopAppBar(
    context: Context,
    imageViewModel: ImageViewModel,
    adViewModel: AdViewModel,
    selectedColor: Color?,
    selectedImage: Uri?,
    snackbarHostState: SnackbarHostState,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    showInterstitialAd: () -> Unit,
) {
    val isAdShow by adViewModel.isAdShown.observeAsState(false)

    CenterAlignedTopAppBar(
        modifier = Modifier.heightIn(max = Sizes.topBarMaxHeight),
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
        title = { Text(stringResource(id = R.string.registerColor)) },
        actions = {
            IconButton(onClick = {
                if (selectedColor != null && selectedImage != null) {

                    val imageBytes = (context.getBitmapFromUri(selectedImage)).toBytes()
                    imageViewModel.insert(
                        color = selectedColor.toArgb(),
                        imageBytes = imageBytes,
                    )

                    showInterstitialAd()

                } else {
                    showSnackBar(
                        scope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        message = context.getString(R.string.color_select_message),
                        actionLabel = context.getString(R.string.ok)
                    )
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.save)
                )
            }
        }
    )

    if (isAdShow) {
        LaunchedEffect(isAdShow) {
            adViewModel.resetAdState()
            showSnackBar(
                scope = coroutineScope,
                snackbarHostState = snackbarHostState,
                message = context.getString(R.string.save_message),
                actionLabel = context.getString(R.string.move)
            ) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(PALETTE_STATE)
                }
            }
        }
    }
}