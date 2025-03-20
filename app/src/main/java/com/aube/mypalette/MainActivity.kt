package com.aube.mypalette

import android.content.Context
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
import androidx.navigation.compose.rememberNavController
import com.aube.mypalette.presentation.ui.screens.my_combination.MyCombinationScreen
import com.aube.mypalette.presentation.ui.screens.my_palette.MyPaletteScreen
import com.aube.mypalette.presentation.ui.screens.register_color.RegisterColorScreen
import com.aube.mypalette.presentation.ui.screens.setting.LANGUAGE
import com.aube.mypalette.presentation.ui.screens.setting.SHARED_PREFERENCES
import com.aube.mypalette.presentation.ui.theme.MyPaletteTheme
import com.aube.mypalette.presentation.viewmodel.AdViewModel
import com.aube.mypalette.presentation.viewmodel.ColorViewModel
import com.aube.mypalette.presentation.viewmodel.CombinationViewModel
import com.aube.mypalette.presentation.viewmodel.ImageViewModel
import com.aube.mypalette.utils.setAppLocale
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var interstitialAd: InterstitialAd? = null
    private var lastAdShownTime: Long = 0L

    private val colorViewModel: ColorViewModel by viewModels<ColorViewModel>()
    private val combinationViewModel: CombinationViewModel by viewModels<CombinationViewModel>()
    private val imageViewModel: ImageViewModel by viewModels<ImageViewModel>()
    private val adViewModel: AdViewModel by viewModels<AdViewModel>()

    private val sharedPreferences by lazy {
        getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        loadInterstitialAd()
        val language = sharedPreferences.getString(LANGUAGE, null) ?: Locale.getDefault().language
        setAppLocale(this, language)
        setContent {
            MyPaletteTheme {
                AppContent(
                    colorViewModel,
                    combinationViewModel,
                    imageViewModel,
                    adViewModel,
                    this,
                ) { showInterstitialAd(adViewModel) }
            }
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            getString(R.string.interstitial_ad_unit_id), // 전면 광고 단위 ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    private fun showInterstitialAd(viewModel: AdViewModel) {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastAd = currentTime - lastAdShownTime

        // 1분(60,000ms)이 지나지 않았다면 광고를 표시하지 않음
        if (timeSinceLastAd < 60_000) {
            viewModel.markAdAsShown() // 광고를 건너뛴 경우에도 상태 업데이트
            return
        }
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null // 실패 시 초기화
                loadInterstitialAd() // 다시 로드
                viewModel.markAdAsShown()
            }

            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null // 광고 객체 초기화
                loadInterstitialAd() // 다시 로드
                viewModel.markAdAsShown()
            }
        }
        interstitialAd?.show(this)
        lastAdShownTime = currentTime
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
    adViewModel: AdViewModel,
    context: Context,
    showInterstitialAd: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = REGISTER_STATE)
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

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
                        navController.popBackStack(
                            context.getString(R.string.myCombinationScreen),
                            inclusive = false
                        )
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
                COMBINATION_STATE -> MyCombinationScreen(
                    navController,
                    combinationViewModel,
                    colorViewModel
                )

                PALETTE_STATE -> MyPaletteScreen(colorViewModel, imageViewModel)
                REGISTER_STATE -> RegisterColorScreen(
                    colorViewModel,
                    imageViewModel,
                    adViewModel,
                    pagerState,
                    showInterstitialAd
                )
            }
        }
    }
}