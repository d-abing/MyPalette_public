package com.aube.mypalette.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

object LocalePrefs {
    private val KEY_LOCALE = stringPreferencesKey("app_locale")

    fun flow(context: Context) = context.dataStore.data.map { it[KEY_LOCALE].orEmpty() }

    suspend fun set(context: Context, tag: String) {
        context.dataStore.edit { it[KEY_LOCALE] = tag }
    }
}