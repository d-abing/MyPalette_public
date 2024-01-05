package com.aube.mypalette.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aube.mypalette.ui.theme.MyPaletteTheme
import com.aube.mypalette.viewmodel.ColorViewModel
import com.aube.mypalette.viewmodel.CombinationViewModel

class MainActivity : ComponentActivity() {

    private val colorViewModel: ColorViewModel by viewModels()
    private val combinationViewModel: CombinationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPaletteTheme {
                AppContent(colorViewModel, combinationViewModel)
            }
        }
    }
}

@Composable
fun AppContent(
    colorViewModel: ColorViewModel,
    combinationViewModel: CombinationViewModel
) {
    MyPaletteNavGraph(colorViewModel, combinationViewModel)
}

@Composable
fun MyPaletteNavGraph(
    colorViewModel: ColorViewModel,
    combinationViewModel: CombinationViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                // 내 팔레트 버튼
                NavigationBarItem(
                    selected = navController.currentDestination?.route == "myPaletteScreen",
                    onClick = {
                        navController.navigate("myPaletteScreen")
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("내 팔레트") }
                )

                // 색 등록하기 버튼
                NavigationBarItem(
                    selected = navController.currentDestination?.route == "registerColorScreen",
                    onClick = {
                        navController.navigate("registerColorScreen")
                    },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                    label = { Text("색 등록하기") }
                )

                // 색 추출하기 버튼
                NavigationBarItem(
                    selected = navController.currentDestination?.route == "extractColorScreen",
                    onClick = {
                        navController.navigate("extractColorScreen")
                    },
                    icon = { Icon(Icons.Default.Send, contentDescription = null) },
                    label = { Text("색 추출하기") }
                )

                // 나만의 조합 버튼
                NavigationBarItem(
                    selected = navController.currentDestination?.route == "myCombinationScreen",
                    onClick = {
                        navController.navigate("myCombinationScreen")
                    },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("나만의 조합") }
                )
            }
        }
    ) { _ ->
        // NavHost 내용을 이곳에 놓습니다.
        NavHost(
            navController = navController,
            startDestination = "myPaletteScreen"
        ) {
            composable("myPaletteScreen") {
                MyPaletteScreen(
                    colorViewModel = colorViewModel
                )
            }
            composable("registerColorScreen") {
                RegisterColorScreen(
                    colorViewModel = colorViewModel
                )
            }
            composable("extractColorScreen") {
                ExtractColorScreen(
                    colorViewModel = colorViewModel
                )
            }
            composable("myCombinationScreen") {
                MyCombinationScreen(
                    combinationViewModel = combinationViewModel
                )
            }
        }
    }
}