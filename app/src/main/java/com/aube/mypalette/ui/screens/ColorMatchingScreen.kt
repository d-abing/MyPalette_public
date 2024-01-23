package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.aube.mypalette.viewmodel.ColorViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorMatchingScreen(
    colorViewModel: ColorViewModel
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Color Matching") }
            )
        },
        content = {
            // 여기에 색 매칭 기능 구현
            Text("Color Matching Screen Content")
        }
    )
}