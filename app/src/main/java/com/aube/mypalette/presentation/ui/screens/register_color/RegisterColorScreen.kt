package com.aube.mypalette.presentation.ui.screens.register_color

import android.content.ContentValues
import android.content.Context
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
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Scroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.screens.register_color.content.RegisterColorContent
import com.aube.mypalette.presentation.ui.screens.register_color.top_app_bar.RegisterColorTopAppBar
import com.aube.mypalette.presentation.viewmodel.ColorViewModel
import com.aube.mypalette.presentation.viewmodel.ImageViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RegisterColorScreen(
    colorViewModel: ColorViewModel,
    imageViewModel: ImageViewModel,
    pagerState: PagerState,
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

    val photoFromCameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                resetSimilarColorResult(similarColorResult)
                photoUri?.let { uri ->
                    // 성공적으로 저장된 URI 처리
                    selectedImage = uri
                }
            }
            photoUri = null
        }
    val photoFromGalleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                resetSimilarColorResult(similarColorResult)
                selectedImage = it
            }
        }

    Scroll
    Scaffold(
        topBar = {
            RegisterColorTopAppBar(
                context,
                imageViewModel,
                selectedColor,
                selectedImage,
                snackbarHostState,
                pagerState,
                coroutineScope
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        RegisterColorContent(
            innerPadding = innerPadding,
            selectedColor = selectedColor,
            colorPalette = colorPalette,
            selectedImage = selectedImage,
            context = context,
            colorViewModel = colorViewModel,
            lifecycleOwner = lifecycleOwner,
            similarColorResult = similarColorResult,
            onColorSelected = { selectedColor = it },
            onCameraClick = {
                photoUri = createImageUri(context, context.getString(R.string.uri_display_name))
                photoUri?.let {
                    photoFromCameraLauncher.launch(it)
                }
            },
            onGalleryClick = { photoFromGalleryLauncher.launch(context.getString(R.string.gallery_launcher_input)) }
        )
    }
}

fun resetSimilarColorResult(similarColorResult: MutableState<Pair<Color?, Double?>>) {
    similarColorResult.value = Color(0) to null
}

fun createImageUri(context: Context, displayName: String): Uri? {
    val contentValues = ContentValues().apply {
        put(
            MediaStore.Images.Media.DISPLAY_NAME,
            context.getString(R.string.file_name, displayName)
        )
        put(MediaStore.Images.Media.MIME_TYPE, context.getString(R.string.mime_type))
        put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES
        ) // Android 10 이상에서 저장 위치 지정
    }
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
}