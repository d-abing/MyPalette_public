package com.aube.mypalette.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object AppLocaleManager {
    fun apply(languageTag: String?) {
        val tags = languageTag?.takeIf { it.isNotBlank() } ?: ""
        val localeList = LocaleListCompat.forLanguageTags(tags)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}