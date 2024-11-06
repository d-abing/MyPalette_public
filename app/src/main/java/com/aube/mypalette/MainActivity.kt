package com.aube.mypalette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aube.mypalette.presentation.ui.screens.my_combination.MyCombinationScreen
import com.aube.mypalette.presentation.ui.screens.my_palette.MyPaletteScreen
import com.aube.mypalette.presentation.ui.screens.register_color.RegisterColorScreen
import com.aube.mypalette.presentation.ui.theme.MyPaletteTheme
import com.aube.mypalette.presentation.viewmodel.ColorViewModel
import com.aube.mypalette.presentation.viewmodel.CombinationViewModel
import com.aube.mypalette.presentation.viewmodel.ImageViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val colorViewModel: ColorViewModel by viewModels<ColorViewModel>()
    private val combinationViewModel: CombinationViewModel by viewModels<CombinationViewModel>()
    private val imageViewModel: ImageViewModel by viewModels<ImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyPaletteTheme {
                AppContent(colorViewModel, combinationViewModel, imageViewModel)
            }
        }
    }
}

const val COMBINATION_STATE = 0
const val PALETTE_STATE = 1
const val REGISTER_STATE = 2

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AppContent(
    colorViewModel: ColorViewModel,
    combinationViewModel: CombinationViewModel,
    imageViewModel: ImageViewModel,
) {
    val pagerState = rememberPagerState(initialPage = REGISTER_STATE)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.heightIn(max = 50.dp),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = stringResource(id = R.string.myCombination)
                        )
                    },
                    selected = pagerState.currentPage == COMBINATION_STATE,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(COMBINATION_STATE)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_palette_24),
                            contentDescription = stringResource(id = R.string.myPalette)
                        )
                    },
                    selected = pagerState.currentPage == PALETTE_STATE,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(PALETTE_STATE)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = stringResource(id = R.string.registerColor)
                        )
                    },
                    selected = pagerState.currentPage == REGISTER_STATE,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(REGISTER_STATE)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (page) {
                COMBINATION_STATE -> MyCombinationScreen(combinationViewModel, colorViewModel)
                PALETTE_STATE -> MyPaletteScreen(colorViewModel, imageViewModel)
                REGISTER_STATE -> RegisterColorScreen(colorViewModel, imageViewModel, pagerState)
            }
        }
    }
}

