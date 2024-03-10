package com.aube.mypalette.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aube.mypalette.R
import com.aube.mypalette.database.MyPaletteDatabase
import com.aube.mypalette.repository.ColorRepository
import com.aube.mypalette.repository.CombinationRepository
import com.aube.mypalette.repository.ImageRepository
import com.aube.mypalette.ui.screens.MyCombinationScreen
import com.aube.mypalette.ui.screens.MyPaletteScreen
import com.aube.mypalette.ui.screens.RegisterColorScreen
import com.aube.mypalette.ui.theme.MyPaletteTheme
import com.aube.mypalette.viewmodel.ColorViewModel
import com.aube.mypalette.viewmodel.ColorViewModelFactory
import com.aube.mypalette.viewmodel.CombinationViewModel
import com.aube.mypalette.viewmodel.CombinationViewModelFactory
import com.aube.mypalette.viewmodel.ImageViewModel
import com.aube.mypalette.viewmodel.ImageViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = MyPaletteDatabase.getInstance(this)
        val colorRepository = ColorRepository(database.colorDao())
        val combinationRepository = CombinationRepository(database.combinationDao())
        val imageRepository = ImageRepository(database.imageDao())
        val lifecycleOwner = this

        setContent {
            MyPaletteTheme {
                AppContent(colorRepository, combinationRepository, imageRepository, lifecycleOwner)
            }
        }
    }
}

@Composable
fun AppContent(
    colorRepository: ColorRepository,
    combinationRepository: CombinationRepository,
    imageRepository: ImageRepository,
    lifecycleOwner: LifecycleOwner
) {
    val colorViewModel: ColorViewModel = viewModel(factory = ColorViewModelFactory(colorRepository))
    val combinationViewModel: CombinationViewModel = viewModel(factory = CombinationViewModelFactory(combinationRepository))
    val imageViewModel: ImageViewModel = viewModel(factory = ImageViewModelFactory(imageRepository))
    val context: Context = LocalContext.current

    MyPaletteNavGraph(colorViewModel, combinationViewModel, imageViewModel, context, lifecycleOwner)
}

@Composable
fun MyPaletteNavGraph(
    colorViewModel: ColorViewModel,
    combinationViewModel: CombinationViewModel,
    imageViewModel: ImageViewModel,
    context: Context,
    lifecycleOwner: LifecycleOwner
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
            ) {
                // 나만의 조합 버튼
                NavigationBarItem(
                    selected = navController.currentDestination?.route == stringResource(id = R.string.myCombinationScreen),
                    onClick = {
                        navController.navigate(context.getString(R.string.myCombinationScreen)) {
                            popUpTo(context.getString(R.string.myCombinationScreen)) {
                                inclusive = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.myCombination)) }
                )

                // 내 팔레트 버튼
                NavigationBarItem(
                    selected = navController.currentDestination?.route == stringResource(id = R.string.myPaletteScreen),
                    onClick = {
                        navController.navigate(context.getString(R.string.myPaletteScreen)) {
                            popUpTo(context.getString(R.string.myPaletteScreen)) {
                                inclusive = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                        painter = painterResource(R.drawable.baseline_palette_24),
                        contentDescription = null
                        )},
                    label = { Text(stringResource(id = R.string.myPalette)) }
                )

                // 색 등록하기 버튼
                NavigationBarItem(
                    selected = navController.currentDestination?.route == stringResource(id = R.string.registerColorScreen),
                    onClick = {
                        navController.navigate(context.getString(R.string.registerColorScreen)) {
                            popUpTo(context.getString(R.string.registerColorScreen)) {
                                inclusive = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                    label = { Text(stringResource(id = R.string.registerColor)) }
                )


            }
        }
    ) { innerPadding ->
        // NavHost 내용을 이곳에 놓습니다.
        NavHost(
            navController = navController,
            startDestination = stringResource(id = R.string.myPaletteScreen),
            Modifier.padding(innerPadding)
        ) {
            composable(context.getString(R.string.myCombinationScreen)) {
                MyCombinationScreen(
                    combinationViewModel = combinationViewModel,
                    colorViewModel = colorViewModel,
                    lifecycleOwner = lifecycleOwner,
                    context = context,
                )
            }
            composable(context.getString(R.string.myPaletteScreen)) {
                MyPaletteScreen(
                    colorViewModel = colorViewModel,
                    imageViewModel = imageViewModel
                )
            }
            composable(context.getString(R.string.registerColorScreen)) {
                RegisterColorScreen(
                    colorViewModel = colorViewModel,
                    imageViewModel = imageViewModel,
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    navController = navController,
                )
            }
        }
    }
}
