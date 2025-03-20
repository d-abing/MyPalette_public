package com.aube.mypalette.presentation.ui.screens.register_color

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.screens.register_color.content.RegisterColorContent
import com.aube.mypalette.presentation.ui.screens.register_color.top_app_bar.RegisterColorTopAppBar
import com.aube.mypalette.presentation.viewmodel.AdViewModel
import com.aube.mypalette.presentation.viewmodel.ColorViewModel
import com.aube.mypalette.presentation.viewmodel.ImageViewModel
import com.aube.mypalette.utils.calculateColorDistance
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.UUID

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RegisterColorScreen(
    colorViewModel: ColorViewModel,
    imageViewModel: ImageViewModel,
    adViewModel: AdViewModel,
    pagerState: PagerState,
    showInterstitialAd: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var selectedColor by remember { mutableStateOf<Color?>(null) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val colorPalette = remember { List(7) { mutableStateOf(Color.White) } }
    val similarColorResult = remember { mutableStateOf<Pair<Color?, Double?>>(Color(0) to null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                resetSimilarColorResult(similarColorResult)
                selectedImage = it
            }
        }
    }

    val photoFromCameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let { uri ->
                    startCrop(context, uri) { uCropIntent ->
                        cropImageLauncher.launch(uCropIntent)
                    }
                }
            }
            photoUri = null
        }

    val photoFromGalleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                startCrop(context, it) { uCropIntent ->
                    cropImageLauncher.launch(uCropIntent)
                }
            }
        }

    Scaffold(topBar = {
        RegisterColorTopAppBar(
            context,
            imageViewModel,
            adViewModel,
            selectedColor,
            selectedImage,
            snackbarHostState,
            pagerState,
            coroutineScope,
            showInterstitialAd
        )
    }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        RegisterColorContent(innerPadding = innerPadding,
            selectedColor = selectedColor,
            colorPalette = colorPalette,
            selectedImage = selectedImage,
            context = context,
            onColorPicked = { color ->
                if (selectedImage != null && color.alpha != 0.0f) {
                    resetSimilarColorResult(similarColorResult)
                    selectedColor = color
                    colorViewModel.allColors.observe(lifecycleOwner) { colors ->
                        var closestColor: Color? = null
                        var minDistance: Double? = null

                        colors.forEach { colorEntity ->
                            val databaseColor = Color(colorEntity.color)
                            val distance =
                                calculateColorDistance(color, databaseColor)

                            if (minDistance == null || distance < minDistance!!) {
                                minDistance = distance
                                closestColor = databaseColor
                            }
                        }

                        similarColorResult.value = Pair(closestColor, minDistance)
                    }
                }
            },
            similarColorResult = similarColorResult,
            onCameraClick = {
                photoUri = createImageUri(context, context.getString(R.string.uri_display_name))
                photoUri?.let {
                    photoFromCameraLauncher.launch(it)
                }
            },
            onGalleryClick = { photoFromGalleryLauncher.launch(context.getString(R.string.gallery_launcher_input)) })
    }
}

fun resetSimilarColorResult(similarColorResult: MutableState<Pair<Color?, Double?>>) {
    similarColorResult.value = Color(0) to null
}

fun createImageUri(context: Context, displayName: String): Uri? {
    val contentValues = ContentValues().apply {
        put(
            MediaStore.Images.Media.DISPLAY_NAME, context.getString(R.string.file_name, displayName)
        )
        put(MediaStore.Images.Media.MIME_TYPE, context.getString(R.string.mime_type))
        put(
            MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES
        )
    }
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
    )
}

fun startCrop(
    context: Context,
    sourceUri: Uri,
    launch: (Intent) -> Unit,
) {
    val destinationUri = Uri.fromFile(
        File(
            context.cacheDir,
            context.getString(
                R.string.file_name,
                context.getString(R.string.uri_display_name) + "${UUID.randomUUID()}"
            )
        )
    )
    val uCropIntent = UCrop.of(sourceUri, destinationUri).withAspectRatio(1f, 1f) // 정사각형 비율
        .getIntent(context)
    launch(uCropIntent)
}