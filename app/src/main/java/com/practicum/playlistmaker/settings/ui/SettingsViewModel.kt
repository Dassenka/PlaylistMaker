package com.practicum.playlistmaker.settings.ui


import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor


class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
    application: App
) : AndroidViewModel(application) {

    private val _themeSettingsLiveData = MutableLiveData<ThemeSettings>()
    val themeSettingsLiveData: LiveData<ThemeSettings> = _themeSettingsLiveData

    init {
        _themeSettingsLiveData.postValue(settingsInteractor.getThemeSettings())
    }

    fun switchThemeVM(darkThemeEnabled: Boolean) {
        var darkTheme = ThemeSettings(darkThemeEnabled)
        _themeSettingsLiveData.postValue(darkTheme)
        settingsInteractor.updateThemeSetting(darkTheme)
        App.switchTheme(darkThemeEnabled)
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

    companion object {

        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[APPLICATION_KEY] as App)
                SettingsViewModel(
                    Creator.provideSettingsInteractor(context),
                    Creator.provideSharingInteractor(context),
                    application
                )
            }
        }
    }
}