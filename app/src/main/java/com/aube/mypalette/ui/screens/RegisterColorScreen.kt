package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterColorScreen(
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Color") }
            )
        },
        content = {
            // 여기에 색 등록하기 화면 구현
            Text("Register Color Screen Content")
        }
    )
}