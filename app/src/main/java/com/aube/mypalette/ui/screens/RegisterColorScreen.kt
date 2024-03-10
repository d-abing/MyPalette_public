package com.aube.mypalette.ui.screens

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.aube.mypalette.R
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.database.ImageEntity
import com.aube.mypalette.utils.calculateColorDistance
import com.aube.mypalette.utils.getBitmapFromUri
import com.aube.mypalette.utils.observeOnce
import com.aube.mypalette.utils.showSnackBar
import com.aube.mypalette.utils.toBytes
import com.aube.mypalette.viewmodel.ColorViewModel
import com.aube.mypalette.viewmodel.ImageViewModel
import java.io.IOException


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterColorScreen(
    colorViewModel: ColorViewModel,
    imageViewModel: ImageViewModel,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    navController: NavController,
) {
    var selectedColor: Color? by remember { mutableStateOf(null) }
    var selectedImage: Uri? by remember { mutableStateOf(null) }
    var imageBytes: ByteArray? by remember { mutableStateOf(null) }
    val colorPalette = List(7) { remember { mutableStateOf(Color.White) } }.toMutableList()
    var count: Int? by remember { mutableStateOf(0) }
    val similarColorResult = remember { mutableStateOf<Pair<Color?, Double?>>(Color(0) to null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val photoFromCameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                similarColorResult.value = Color(0) to null
                selectedImage = saveBitmapToGalleryAndGetUri(bitmap, "PaletteImage", context)
            }
        }
    val photoFromGalleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            similarColorResult.value = Color(0) to null
            selectedImage = uri
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Register Color") },
                actions = {
                    IconButton(onClick = {
                        var saveToggle = false

                        if (selectedColor != null && selectedImage != null) {
                            colorViewModel.colorId.observeOnce(lifecycleOwner, Observer { colorId ->
                                if (colorId == null) {
                                    colorViewModel.insert(
                                        ColorEntity(
                                            color = selectedColor!!.toArgb()
                                        )
                                    )
                                }
                            })
                            colorViewModel.colorId.observe(lifecycleOwner, Observer { colorId ->
                                if (!saveToggle && colorId != null) {
                                    imageViewModel.insert(
                                        ImageEntity(
                                            imageBytes = imageBytes!!,
                                            colorId = colorId,
                                        )
                                    )
                                    saveToggle = true
                                    showSnackBar(scope, snackbarHostState,
                                        context.getString(R.string.save_message),
                                        context.getString(R.string.move)){
                                        navController.navigate(context.getString(R.string.myPaletteScreen))
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
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(selectedColor ?: Color.White)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 이미지 박스 배치
            imageBytes = imageBox(selectedImage, context, colorPalette)


            // 팔레트 배치
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 20.dp)
            ) {

                var colorCount = 0
                for (color in colorPalette) {
                    var boxColor: Color?
                    var noColor = false
                    if (color.value.alpha != 0.0f) {
                        boxColor = color.value
                        colorCount += 1
                    } else {
                        boxColor = Color.White
                        noColor = true
                    }

                    if (!noColor) {
                        Box(
                            modifier = Modifier
                                .size(49.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp))
                                .background(boxColor)
                                .clickable {
                                    if (selectedImage != null) {
                                        similarColorResult.value = Color(0) to null // 클릭할 때마다 초기화
                                        selectedColor = color.value
                                        colorViewModel.checkIdForColor(selectedColor!!.toArgb())
                                        colorViewModel.allColors.observe(lifecycleOwner) { colors ->
                                            var closestColor: Color? = null
                                            var minDistance: Double? = null

                                            colors.forEach { colorEntity ->
                                                val databaseColor = Color(colorEntity.color)
                                                val distance =
                                                    calculateColorDistance(
                                                        selectedColor!!,
                                                        databaseColor
                                                    )

                                                if (minDistance == null || distance < minDistance!!) {
                                                    minDistance = distance
                                                    closestColor = databaseColor
                                                }
                                            }

                                            similarColorResult.value =
                                                Pair(closestColor, minDistance)
                                        }
                                    }
                                }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp))
                                .background(boxColor)
                                .clickable {
                                    similarColorResult.value = Color(0) to null
                                }
                            ,
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(id = R.string.no),
                            )
                        }
                    }
                }

                count = colorCount
            }

            // 유사한 색상 정보 표시
            SimilarityColor(count!!, selectedImage, similarColorResult)


            // 버튼 배치
            CameraAndGalleryButton(photoFromCameraLauncher, photoFromGalleryLauncher)
        }
    }
}

@Composable
fun imageBox(selectedImage: Uri?, context: Context, colorPalette: MutableList<MutableState<Color>>): ByteArray? {
    if (selectedImage != null) {
        Image(
            painter = rememberAsyncImagePainter(selectedImage),
            contentDescription = "Selected Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(350.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        val bitmap = context.getBitmapFromUri(selectedImage)

        Palette.from(bitmap!!).generate { palette ->
            val dominantSwatch = palette?.dominantSwatch?.rgb ?: 0
            val darkMutedSwatch = palette?.darkMutedSwatch?.rgb ?: 0
            val darkVibrantSwatch = palette?.darkVibrantSwatch?.rgb ?: 0
            val lightMutedSwatch = palette?.lightMutedSwatch?.rgb ?: 0
            val lightVibrantSwatch = palette?.lightVibrantSwatch?.rgb ?: 0
            val mutedSwatch = palette?.mutedSwatch?.rgb ?: 0
            val vibrantSwatch = palette?.vibrantSwatch?.rgb ?: 0

            colorPalette[0].value = Color(dominantSwatch)
            colorPalette[1].value = Color(darkMutedSwatch)
            colorPalette[2].value = Color(darkVibrantSwatch)
            colorPalette[3].value = Color(lightMutedSwatch)
            colorPalette[4].value = Color(lightVibrantSwatch)
            colorPalette[5].value = Color(mutedSwatch)
            colorPalette[6].value = Color(vibrantSwatch)
        }

        return bitmap.toBytes()

    } else {
        Box(
            modifier = Modifier
                .size(350.dp)
                .background(Color.Gray, RoundedCornerShape(16.dp))
        )

        return null
    }
}

@Composable
fun SimilarityColor(count: Int, selectedImage: Uri?, similarColorResult: MutableState<Pair<Color?, Double?>>) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val (similarColor, distance) = similarColorResult.value

            if (similarColor != null && distance != null) {
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
                        text = stringResource(id = R.string.yes_similarity_message, similarityPercentage),
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    Text(text = stringResource(id = R.string.no_similarity_message), modifier = Modifier.padding(8.dp))
                }
            } else if (selectedImage == null){
                Text(
                    text = stringResource(id = R.string.comparison_message),
                    modifier = Modifier
                        .padding(8.dp),
                )
            } else {
                Text(
                    text = stringResource(id = R.string.extracted_colors_message, count),
                    modifier = Modifier
                        .padding(8.dp),
                )
            }
        }
    }
}

@Composable
fun CameraAndGalleryButton(photoFromCameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>, photoFromGalleryLauncher: ManagedActivityResultLauncher<String, Uri?>) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        // 촬영하여 등록하기 버튼
        Button(
            onClick = {
                photoFromCameraLauncher.launch()
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .height(70.dp)
                .weight(1f)
        ) {
            Icon(painter = painterResource(R.drawable.baseline_photo_camera_24), contentDescription = stringResource(
                id = R.string.camera
            ), Modifier.padding(5.dp))
            Text(stringResource(id = R.string.from_camera))
        }

        Spacer(Modifier.width(10.dp))

        // 사진첩에서 찾아보기 버튼
        Button(
            onClick = {
                photoFromGalleryLauncher.launch("image/*")
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .height(70.dp)
                .weight(1f)
        ) {
            Icon(painter = painterResource(R.drawable.baseline_image_24), stringResource(
                id = R.string.gallery
            ), Modifier.padding(5.dp))
            Text(stringResource(id = R.string.from_gallery))
        }
    }
}

fun saveBitmapToGalleryAndGetUri(bitmap: Bitmap, displayName: String, context: Context): Uri? {
    val resolver = context?.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    }

    val imageUri = resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    try {
        val outputStream = resolver?.openOutputStream(imageUri!!)
        if (outputStream != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        outputStream?.close()

        return imageUri
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return null
}