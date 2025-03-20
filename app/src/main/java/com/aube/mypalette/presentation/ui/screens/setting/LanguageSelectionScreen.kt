package com.aube.mypalette.presentation.ui.screens.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.theme.Paddings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    onBackClick: () -> Unit,
    onLanguageSelected: (String) -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = { Text(getString(context, R.string.choose_language)) }
            )
        }
    ) { innerPadding ->
        val languages = listOf(
            "English" to "en",
            "العربية" to "ar",
            "Deutsch" to "de",
            "Español" to "es",
            "Français" to "fr",
            "हिन्दी" to "hi",
            "Bahasa Indonesia" to "id",
            "Italiano" to "it",
            "日本語" to "ja",
            "한국어" to "ko",
            "Polski" to "pl",
            "Português" to "pt",
            "ภาษาไทย" to "th",
            "Türkçe" to "tr",
            "Tiếng Việt" to "vi",
            "中文 (简体)" to "zh"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Paddings.xlarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Paddings.small),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(languages) { (displayName, localeCode) ->
                    Button(
                        onClick = { onLanguageSelected(localeCode) },
                        modifier = Modifier.padding(Paddings.medium)
                    ) {
                        Text(text = displayName, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}