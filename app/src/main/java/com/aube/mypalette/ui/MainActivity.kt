package com.aube.mypalette.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aube.mypalette.ui.screens.ColorMatchingScreen
import com.aube.mypalette.ui.screens.MyCombinationScreen
import com.aube.mypalette.ui.screens.MyPaletteScreen
import com.aube.mypalette.ui.screens.RegisterColorScreen
import com.aube.mypalette.ui.theme.MyPaletteTheme
import com.aube.mypalette.viewmodel.ColorViewModel
import com.aube.mypalette.viewmodel.CombinationViewModel
import com.aube.mypalette.repository.ColorRepository
import com.aube.mypalette.repository.CombinationRepository
import com.aube.mypalette.database.MyPaletteDatabase
import com.aube.mypalette.viewmodel.ColorViewModelFactory
import com.aube.mypalette.viewmodel.CombinationViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = MyPaletteDatabase.getInstance(this)
        val colorRepository = ColorRepository(database.colorDao())
        val combinationRepository = CombinationRepository(database.combinationDao())

        setContent {
            MyPaletteTheme {
                AppContent(colorRepository, combinationRepository)
            }
        }
    }
}

@Composable
fun AppContent(
    colorRepository: ColorRepository,
    combinationRepository: CombinationRepository
) {
    val colorViewModel: ColorViewModel = viewModel(factory = ColorViewModelFactory(colorRepository))
    val combinationViewModel: CombinationViewModel = viewModel(factory = CombinationViewModelFactory(combinationRepository))

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

                // 색 찾기 버튼
                NavigationBarItem(
                    selected = navController.currentDestination?.route == "colorMatchingScreen",
                    onClick = {
                        navController.navigate("colorMatchingScreen")
                    },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("색 찾기") }
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
    ) { innerPadding ->
        // NavHost 내용을 이곳에 놓습니다.
        NavHost(
            navController = navController,
            startDestination = "myPaletteScreen",
            Modifier.padding(innerPadding)
        ) {
            composable("myPaletteScreen") {
                MyPaletteScreen(
                    colorViewModel = colorViewModel
                )
            }
            composable("colorMatchingScreen") {
                ColorMatchingScreen(
                    colorViewModel = colorViewModel
                )
            }
            composable("registerColorScreen") {
                RegisterColorScreen(
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
