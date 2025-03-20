package com.aube.mypalette.presentation.ui.screens.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aube.mypalette.MainActivity
import com.aube.mypalette.presentation.ui.theme.MyPaletteTheme
import com.aube.mypalette.utils.setAppLocale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPaletteTheme {
                SettingContent(this)
            }
        }
    }
}

@Composable
fun SettingContent(
    context: Context,
) {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "settings",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Settings 화면
            composable("settings") {
                SettingScreen(
                    onBackClick = {
                        (context as? SettingActivity)?.finish()
                    },
                    onLanguageSelectionClick = {
                        navController.navigate("language_selection")
                    }
                )
            }

            // Language Selection 화면
            composable("language_selection") {
                LanguageSelectionScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLanguageSelected = { selectedLanguage ->
                        setAppLocale(context, selectedLanguage)
                        val sharedPreferences = context.getSharedPreferences(
                            SHARED_PREFERENCES,
                            Context.MODE_PRIVATE
                        )
                        sharedPreferences.edit().putString(LANGUAGE, selectedLanguage).apply()
                        val restartIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(restartIntent)
                    }
                )
            }
        }
    }
}

const val LANGUAGE = "language"
const val SHARED_PREFERENCES = "MyPalettePreferences"