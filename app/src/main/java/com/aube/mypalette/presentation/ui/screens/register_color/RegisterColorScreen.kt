package com.aube.mypalette.presentation.ui.screens.register_color

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.toArgb
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
import java.text.SimpleDateFormat
import java.util.Locale
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

    val cropOptions = UCrop.Options()
        .apply { setActiveControlsWidgetColor(MaterialTheme.colorScheme.primary.toArgb()) }

    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data ?: return@rememberLauncherForActivityResult)
            if (resultUri != null) {
                resetSimilarColorResult(similarColorResult)
                selectedImage = resultUri
            }
        }
    }

    val photoFromCameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            val current = photoUri
            if (success && current != null) {
                startCrop(context, current, cropOptions) { uCropIntent ->
                    cropImageLauncher.launch(uCropIntent)
                }
            } else if (current != null) {
                // 촬영 취소/실패 시 방금 insert한 항목 삭제
                context.contentResolver.delete(current, null, null)
            }
            photoUri = null
        }

    val photoFromGalleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                startCrop(context, it, cropOptions) { uCropIntent ->
                    cropImageLauncher.launch(uCropIntent)
                }
            }
        }

    Scaffold(
        topBar = {
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        RegisterColorContent(
            innerPadding = innerPadding,
            selectedColor = selectedColor,
            colorPalette = colorPalette,
            selectedImage = selectedImage,
            context = context,
            onColorPicked = { color ->
                if (selectedImage != null && color.alpha != 0.0f) {
                    resetSimilarColorResult(similarColorResult)
                    selectedColor = color
                    // TODO: View에서 LiveData observe하는 건 비권장. VM에서 State로 변환 권장.
                    colorViewModel.allColors.observe(lifecycleOwner) { colors ->
                        var closestColor: Color? = null
                        var minDistance: Double? = null
                        colors.forEach { colorEntity ->
                            val databaseColor = Color(colorEntity.color)
                            val distance = calculateColorDistance(color, databaseColor)
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
                photoUri = createImageUri(context) // 고유 파일명으로 생성
                photoUri?.let { photoFromCameraLauncher.launch(it) }
            },
            onGalleryClick = {
                photoFromGalleryLauncher.launch("image/*")
            }
        )
    }
}

fun resetSimilarColorResult(similarColorResult: MutableState<Pair<Color?, Double?>>) {
    similarColorResult.value = Color(0) to null
}

/**
 * 카메라 촬영용 MediaStore URI 생성
 * - 고유 파일명(타임스탬프) 보장
 * - 정확한 MIME (image/jpeg)
 * - Android 10+ 에서는 Pictures/MyPalette 하위로 저장
 */
fun createImageUri(context: Context): Uri? {
    val resolver = context.contentResolver
    val name = "IMG_" + SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US)
        .format(System.currentTimeMillis()) + ".jpg"

    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/MyPalette"
            )
            // 촬영 중 표시를 쓰고 싶다면 주석 해제 후, 저장 완료 시 0으로 업데이트
            // put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
}

/**
 * uCrop 시작
 */
fun startCrop(
    context: Context,
    sourceUri: Uri,
    options: UCrop.Options = UCrop.Options(),
    launch: (Intent) -> Unit,
) {
    val destinationUri = Uri.fromFile(
        File(
            context.cacheDir,
            context.getString(
                R.string.file_name,
                context.getString(R.string.uri_display_name) + "_${UUID.randomUUID()}"
            )
        )
    )

    val uCropIntent = UCrop.of(sourceUri, destinationUri)
        .withAspectRatio(1f, 1f) // 정사각형 고정
        .withOptions(options)
        .getIntent(context)

    launch(uCropIntent)
}
