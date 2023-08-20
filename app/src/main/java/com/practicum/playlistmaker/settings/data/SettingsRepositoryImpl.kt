package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(val context: Context) : SettingsRepository {

    var sharedPrefs = context.getSharedPreferences(DAY_NIGHT_THEME_PREFERENCES, MODE_PRIVATE)
    var darkTheme = sharedPrefs.getBoolean(DAY_NIGHT_THEME_KEY, false)

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(darkTheme)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        AppCompatDelegate.setDefaultNightMode(
            if (settings.darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit()
            .putBoolean(DAY_NIGHT_THEME_KEY, darkTheme)
            .apply()

    }

    companion object {
        const val DAY_NIGHT_THEME_PREFERENCES = "preferences"
        const val DAY_NIGHT_THEME_KEY = "key"
    }
}