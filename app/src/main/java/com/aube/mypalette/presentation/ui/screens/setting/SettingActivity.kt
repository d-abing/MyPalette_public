package com.aube.mypalette.presentation.ui.screens.setting

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.aube.mypalette.data.datastore.LocalePrefs
import com.aube.mypalette.presentation.ui.theme.MyPaletteTheme
import com.aube.mypalette.utils.AppLocaleManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val tag = kotlinx.coroutines.runBlocking {
            LocalePrefs.flow(applicationContext).first()  // import kotlinx.coroutines.flow.first
        }
        AppLocaleManager.apply(tag)

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
    SettingScreen(
        onBackClick = {
            (context as? SettingActivity)?.finish()
        },
    )
}

const val LANGUAGE = "language"
const val SHARED_PREFERENCES = "MyPalettePreferences"