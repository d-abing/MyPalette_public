package com.aube.mypalette.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.aube.mypalette.viewmodel.CombinationViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MyCombinationScreen(
    combinationViewModel: CombinationViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Combinations") }
            )
        },
        content = {
            // 여기에 나만의 조합 화면 구현
            Text("My Combination Screen Content")
        }
    )
}