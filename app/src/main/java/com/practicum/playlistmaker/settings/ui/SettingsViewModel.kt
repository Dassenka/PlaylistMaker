package com.practicum.playlistmaker.settings.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor


class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val _themeSettingsLiveData = MutableLiveData<ThemeSettings>()
    val themeSettingsLiveData: LiveData<ThemeSettings> = _themeSettingsLiveData

    init {
        _themeSettingsLiveData.postValue(settingsInteractor.getThemeSettings())
    }

    fun switchThemeVM(darkThemeEnabled: Boolean) {
        val darkTheme = ThemeSettings(darkThemeEnabled)
        _themeSettingsLiveData.postValue(darkTheme)
        settingsInteractor.updateThemeSetting(darkTheme)
    }

    fun shareLink(shareAddress: String) {
        sharingInteractor.shareApp(shareAddress)
    }

    fun openTerms(agreementLink: String) {
        sharingInteractor.openTerms(agreementLink)
    }

    fun openSupport(supportAddress: String, supportTheme: String, supportMessage: String) {
        sharingInteractor.openSupport(supportAddress, supportTheme, supportMessage)
    }
}