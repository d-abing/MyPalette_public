package com.aube.mypalette.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aube.mypalette.viewmodel.ColorViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import java.io.File
import java.io.FileOutputStream


@Composable
fun RegisterColorScreen(
    colorViewModel: ColorViewModel
) {
    var openImageChooser by rememberSaveable { mutableStateOf(false) }

    // For gallery image chooser
    val imageCropLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            // Got image data. Use it according to your need
        } else {
            // There is some error while choosing image -> show error accordingly(result.error)
        }
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            val cropOptions = CropImageContractOptions(uri, CropImageOptions())
            imageCropLauncher.launch(cropOptions)
        }

    // For camera image
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { cameraBitmap ->
        cameraBitmap?.let {
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"
            val imageFile = File("", fileName)
            try {
                val out = FileOutputStream(imageFile)
                it.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Got image data. Use it according to your need(imageFile)
        }
    }

    if (openImageChooser) {
        UploadImageAlertDialog(
            onCameraClick = {
                cameraLauncher.launch()
                openImageChooser = false
            },
            onGalleryClick = {
                imagePickerLauncher.launch("image/*")
                openImageChooser = false
            },
            onDismissClick = { openImageChooser = false }
        )
    }

    Button(onClick = { openImageChooser = true }) {
        Text("Select Image")
    }

    // ... (이하 생략)
}


@Composable
fun UploadImageAlertDialog(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissClick() },
        DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.onSurface),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Camera",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .clickable { onCameraClick() }
                )
                Text(
                    text = "Gallery",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .clickable {
                            onGalleryClick()
                        }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
