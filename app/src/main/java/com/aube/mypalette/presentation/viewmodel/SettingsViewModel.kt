package com.aube.mypalette.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aube.mypalette.data.datastore.LocalePrefs
import com.aube.mypalette.data.repository.DriveBackupRepository
import com.aube.mypalette.presentation.ui.screens.setting.DriveState
import com.aube.mypalette.presentation.ui.screens.setting.LanguageOption
import com.aube.mypalette.utils.AppLocaleManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: DriveBackupRepository,
) : ViewModel() {

    private val currentLanguageTag: StateFlow<String> =
        LocalePrefs.flow(context)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val currentLanguage: StateFlow<LanguageOption> =
        currentLanguageTag.map { tag ->
            LanguageOption.values().firstOrNull { it.tag == tag } ?: LanguageOption.SYSTEM
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LanguageOption.SYSTEM)


    private val _drive = MutableStateFlow(DriveState())
    val drive: StateFlow<DriveState> = _drive

    init {
        viewModelScope.launch {
            val acc = repo.restoreAccountName()
            _drive.update {
                it.copy(
                    connected = acc != null,
                    accountName = acc,
                    lastBackup = repo.restoreLastBackupTime()
                )
            }
        }
    }

    fun changeLanguage(option: LanguageOption) {
        viewModelScope.launch {
            LocalePrefs.set(context, option.tag)
            AppLocaleManager.apply(option.tag) // 즉시 적용
        }
    }

    fun onGoogleSignedIn(account: GoogleSignInAccount) {
        viewModelScope.launch {
            repo.persistAccountName(account.email ?: account.displayName ?: "")
            _drive.update { it.copy(connected = true, accountName = repo.restoreAccountName()) }
        }
    }

    fun disconnect(context: Context) {
        viewModelScope.launch {
            _drive.update { it.copy(busy = true, error = null) }
            runCatching {
                repo.signOut(context)
                repo.persistAccountName(null)
            }.onSuccess {
                _drive.value = DriveState()
            }.onFailure { e ->
                _drive.update { it.copy(busy = false, error = e.message) }
            }
        }
    }

    fun backupNow() {
        viewModelScope.launch(Dispatchers.IO) {
            val account = _drive.value.accountName ?: return@launch
            _drive.update { it.copy(busy = true, error = null) }
            runCatching {
                repo.backupNow(account)
            }.onSuccess { ts ->
                _drive.update { it.copy(busy = false, lastBackup = ts) }
            }.onFailure { e ->
                _drive.update { it.copy(busy = false, error = e.message) }
            }
        }
    }

    fun restoreLatest() {
        viewModelScope.launch(Dispatchers.IO) {
            val account = _drive.value.accountName ?: return@launch
            _drive.update { it.copy(busy = true, error = null) }
            runCatching {
                repo.restoreLatest(account)
            }.onSuccess {
                _drive.update { it.copy(busy = false) }
                ProcessPhoenix.triggerRebirth(context)
            }.onFailure { e ->
                _drive.update { it.copy(busy = false, error = e.message) }
            }
        }
    }
}
