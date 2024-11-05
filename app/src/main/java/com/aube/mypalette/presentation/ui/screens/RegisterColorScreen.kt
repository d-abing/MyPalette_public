package com.aube.mypalette.presentation.ui.screens

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.aube.mypalette.R
import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.data.model.ImageEntity
import com.aube.mypalette.presentation.viewmodel.ColorViewModel
import com.aube.mypalette.presentation.viewmodel.ImageViewModel
import com.aube.mypalette.utils.calculateColorDistance
import com.aube.mypalette.utils.getBitmapFromUri
import com.aube.mypalette.utils.observeOnce
import com.aube.mypalette.utils.showSnackBar
import com.aube.mypalette.utils.toBytes
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    val colorPalette = remember { List(7) { mutableStateOf(Color.White) } }
    val similarColorResult = remember { mutableStateOf<Pair<Color?, Double?>>(Color(0) to null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val photoFromCameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                resetSimilarColorResult(similarColorResult)
                selectedImage = saveBitmapToGalleryAndGetUri(it, "PaletteImage", context)
            }
        }
    val photoFromGalleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                resetSimilarColorResult(similarColorResult)
                selectedImage = it
            }
        }

    Scaffold(
        topBar = {
            RegisterColorTopAppBar(
                colorViewModel,
                imageViewModel,
                selectedColor,
                selectedImage,
                imageBytes,
                lifecycleOwner,
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
            onImageBytesChanged = { imageBytes = it },
            onColorSelected = { selectedColor = it },
            photoFromCameraLauncher = photoFromCameraLauncher,
            photoFromGalleryLauncher = photoFromGalleryLauncher
        )
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterColorTopAppBar(
    colorViewModel: ColorViewModel,
    imageViewModel: ImageViewModel,
    selectedColor: Color?,
    selectedImage: Uri?,
    imageBytes: ByteArray?,
    lifecycleOwner: LifecycleOwner,
    snackbarHostState: SnackbarHostState,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = { Text(stringResource(id = R.string.registerColor)) },
        actions = {
            IconButton(onClick = {
                if (selectedColor != null && selectedImage != null) {
                    var saveToggle = false
                    colorViewModel.colorId.observeOnce(lifecycleOwner, Observer { colorId ->
                        if (colorId == null) {
                            colorViewModel.insert(ColorEntity(color = selectedColor.toArgb()))
                        }
                    })
                    colorViewModel.colorId.observe(lifecycleOwner, Observer { colorId ->
                        if (!saveToggle && colorId != null) {
                            imageViewModel.insert(
                                ImageEntity(
                                    imageBytes = imageBytes!!,
                                    colorId = colorId
                                )
                            )
                            saveToggle = true
                            showSnackBar(
                                scope = coroutineScope,
                                snackbarHostState = snackbarHostState,
                                message = context.getString(R.string.save_message),
                                actionLabel = context.getString(R.string.move)
                            ) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            }
                        }
                    })
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(id = R.string.save)
                )
            }
        }
    )
}

@Composable
fun RegisterColorContent(
    innerPadding: PaddingValues,
    selectedColor: Color?,
    colorPalette: List<MutableState<Color>>,
    selectedImage: Uri?,
    context: Context,
    colorViewModel: ColorViewModel,
    lifecycleOwner: LifecycleOwner,
    similarColorResult: MutableState<Pair<Color?, Double?>>,
    onImageBytesChanged: (ByteArray?) -> Unit,
    onColorSelected: (Color) -> Unit,
    photoFromCameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>,
    photoFromGalleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .background(selectedColor ?: Color.White)
            .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            onImageBytesChanged(imageBox(selectedImage, context, colorPalette))
        }

        ColorPaletteRow(
            colorPalette = colorPalette,
            selectedImage = selectedImage,
            colorViewModel = colorViewModel,
            lifecycleOwner = lifecycleOwner,
            similarColorResult = similarColorResult,
            onColorSelected = onColorSelected
        )

        SimilarityColor(
            count = colorPalette.count { it.value.alpha != 0.0f },
            selectedImage = selectedImage,
            similarColorResult = similarColorResult
        )

        CameraAndGalleryButton(
            photoFromCameraLauncher = photoFromCameraLauncher,
            photoFromGalleryLauncher = photoFromGalleryLauncher
        )
    }
}

@Composable
fun imageBox(
    selectedImage: Uri?,
    context: Context,
    colorPalette: List<MutableState<Color>>,
): ByteArray? {

    return if (selectedImage != null) {
        val bitmap = context.getBitmapFromUri(selectedImage)
        Image(
            painter = rememberAsyncImagePainter(
                model = selectedImage
            ),
            contentDescription = stringResource(id = R.string.selected_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        )

        Palette.from(bitmap!!).generate { palette ->
            palette?.let {
                colorPalette[0].value = Color(it.dominantSwatch?.rgb ?: 0)
                colorPalette[1].value = Color(it.darkMutedSwatch?.rgb ?: 0)
                colorPalette[2].value = Color(it.darkVibrantSwatch?.rgb ?: 0)
                colorPalette[3].value = Color(it.lightMutedSwatch?.rgb ?: 0)
                colorPalette[4].value = Color(it.lightVibrantSwatch?.rgb ?: 0)
                colorPalette[5].value = Color(it.mutedSwatch?.rgb ?: 0)
                colorPalette[6].value = Color(it.vibrantSwatch?.rgb ?: 0)
            }
        }

        bitmap.toBytes()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray, RoundedCornerShape(16.dp))
        )
        null
    }
}

@Composable
fun ColorPaletteRow(
    colorPalette: List<MutableState<Color>>,
    selectedImage: Uri?,
    colorViewModel: ColorViewModel,
    lifecycleOwner: LifecycleOwner,
    similarColorResult: MutableState<Pair<Color?, Double?>>,
    onColorSelected: (Color) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp)
            .fillMaxWidth()
    ) {
        colorPalette.forEach { color ->
            val boxColor = if (color.value.alpha != 0.0f) color.value else Color.White
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp))
                    .background(boxColor)
                    .clickable {
                        if (selectedImage != null) {
                            resetSimilarColorResult(similarColorResult)
                            onColorSelected(color.value)
                            colorViewModel.checkIdForColor(color.value.toArgb())
                            colorViewModel.allColors.observe(lifecycleOwner) { colors ->
                                var closestColor: Color? = null
                                var minDistance: Double? = null

                                colors.forEach { colorEntity ->
                                    val databaseColor = Color(colorEntity.color)
                                    val distance =
                                        calculateColorDistance(color.value, databaseColor)

                                    if (minDistance == null || distance < minDistance!!) {
                                        minDistance = distance
                                        closestColor = databaseColor
                                    }
                                }

                                similarColorResult.value = Pair(closestColor, minDistance)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (color.value.alpha == 0.0f) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.no),
                    )
                }
            }
        }
    }
}

@Composable
fun SimilarityColor(
    count: Int,
    selectedImage: Uri?,
    similarColorResult: MutableState<Pair<Color?, Double?>>,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (similarColor, distance) = similarColorResult.value

            when {
                similarColor != null && distance != null -> {
                    val similarityPercentage = ((1 - distance) * 100).toInt()
                    if (similarityPercentage >= 85) {
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp))
                                .background(Color(similarColor.toArgb()))
                        )
                        Text(
                            text = stringResource(
                                id = R.string.yes_similarity_message,
                                similarityPercentage
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.no_similarity_message),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                selectedImage == null -> {
                    Text(
                        text = stringResource(id = R.string.comparison_message),
                        modifier = Modifier.padding(8.dp)
                    )
                }

                else -> {
                    Text(
                        text = stringResource(id = R.string.extracted_colors_message, count),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CameraAndGalleryButton(
    photoFromCameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>,
    photoFromGalleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(top = 10.dp)
    ) {
        Button(
            onClick = { photoFromCameraLauncher.launch() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_photo_camera_24),
                contentDescription = stringResource(id = R.string.camera),
                Modifier.padding(5.dp)
            )
            Text(stringResource(id = R.string.from_camera))
        }

        Spacer(Modifier.width(10.dp))

        Button(
            onClick = { photoFromGalleryLauncher.launch("image/*") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_image_24),
                contentDescription = stringResource(id = R.string.gallery),
                Modifier.padding(5.dp)
            )
            Text(stringResource(id = R.string.from_gallery))
        }
    }
}

fun saveBitmapToGalleryAndGetUri(bitmap: Bitmap, displayName: String, context: Context): Uri? {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    }

    val imageUri = resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    return try {
        resolver?.openOutputStream(imageUri!!).use { outputStream ->
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
        imageUri
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun resetSimilarColorResult(similarColorResult: MutableState<Pair<Color?, Double?>>) {
    similarColorResult.value = Color(0) to null
}
