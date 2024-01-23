package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.aube.mypalette.database.ColorEntity
import com.aube.mypalette.database.ImageEntity
import com.aube.mypalette.viewmodel.ImageViewModel
import com.aube.mypalette.viewmodel.ColorViewModel
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterColorScreen(
    colorViewModel: ColorViewModel,
    imageViewModel: ImageViewModel,
    context: Context
) {
    var selectedColor: Color? by remember { mutableStateOf(null) }
    var selectedImage: Uri? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImage = uri
    }
    var imageBytes: ByteArray? by remember { mutableStateOf(null) }
    var colorId: Int? by remember { mutableStateOf(null) }

    var color0: Color? by remember { mutableStateOf(Color.White) }
    var color1: Color? by remember { mutableStateOf(Color.White) }
    var color2: Color? by remember { mutableStateOf(Color.White) }
    var color3: Color? by remember { mutableStateOf(Color.White) }
    var color4: Color? by remember { mutableStateOf(Color.White) }
    var color5: Color? by remember { mutableStateOf(Color.White) }
    var color6: Color? by remember { mutableStateOf(Color.White) }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Register Color") },
                actions = {
                    IconButton(onClick = {
                        if (selectedColor != null) {
                            colorViewModel.insert(ColorEntity(color = selectedColor!!.toArgb()))
                            imageViewModel.insert(
                                ImageEntity(
                                    imageBytes = imageBytes!!,
                                    colorId = 0!!
                                )
                            )
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Save"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(selectedColor ?: Color.White)
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedImage != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImage),
                    contentDescription = "Selected Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(350.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                val bitmap = context.getBitmapFromUri(selectedImage!!)

                Palette.from(bitmap!!).generate { palette ->
                    val dominantSwatch = palette?.dominantSwatch?.rgb ?: 0
                    val darkMutedSwatch = palette?.darkMutedSwatch?.rgb ?: 0
                    val darkVibrantSwatch = palette?.darkVibrantSwatch?.rgb ?: 0
                    val lightMutedSwatch = palette?.lightMutedSwatch?.rgb ?: 0
                    val lightVibrantSwatch = palette?.lightVibrantSwatch?.rgb ?: 0
                    val mutedSwatch = palette?.mutedSwatch?.rgb ?: 0
                    val vibrantSwatch = palette?.vibrantSwatch?.rgb ?: 0

                    color0 = Color(dominantSwatch)
                    color1 = Color(darkMutedSwatch)
                    color2 = Color(darkVibrantSwatch)
                    color3 = Color(lightMutedSwatch)
                    color4 = Color(lightVibrantSwatch)
                    color5 = Color(mutedSwatch)
                    color6 = Color(vibrantSwatch)
                }

                imageBytes = bitmap.toBytes()

            } else {
                Box(
                    modifier = Modifier
                        .size(350.dp)
                        .background(Color.Gray, RoundedCornerShape(16.dp))
                )
            }

            // 팔레트 배치
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp, top = 20.dp, end = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, Color.DarkGray)
                        .background(color0!!)
                        .clickable {
                            selectedColor = color0
                        }
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, Color.DarkGray)
                        .background(color1!!)
                        .clickable {
                            selectedColor = color1
                        }
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, Color.DarkGray)
                        .background(color2!!)
                        .clickable {
                            selectedColor = color2
                        }
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, Color.DarkGray)
                        .background(color3!!)
                        .clickable {
                            selectedColor = color3
                        }
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, Color.DarkGray)
                        .background(color4!!)
                        .clickable {
                            selectedColor = color4
                        }
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, Color.DarkGray)
                        .background(color5!!)
                        .clickable {
                            selectedColor = color5
                        }
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, Color.DarkGray)
                        .background(color6!!)
                        .clickable {
                            selectedColor = color6
                        }
                )
            }

            // 버튼 배치
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                // 촬영하여 등록하기 버튼
                Button(
                    onClick = {

                    },
                    shape = RoundedCornerShape(16.dp), // 둥글게 만들기
                    modifier = Modifier
                        .height(50.dp)
                ) {
                    Text("촬영하여 등록하기")
                }

                // 사진첩에서 찾아보기 버튼
                Button(
                    onClick = {
                        launcher.launch("image/*")
                    },
                    shape = RoundedCornerShape(16.dp), // 둥글게 만들기
                    modifier = Modifier
                        .height(50.dp)
                ) {
                    Text("사진첩에서 가져오기")
                }
            }
        }
    }
}

fun Context.getBitmapFromUri(uri: Uri): Bitmap? {
    val contentResolver: ContentResolver = this.contentResolver
    try {
        // URI로부터 InputStream을 열어서 BitmapFactory를 사용하여 Bitmap으로 변환
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Bitmap.toBytes(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
