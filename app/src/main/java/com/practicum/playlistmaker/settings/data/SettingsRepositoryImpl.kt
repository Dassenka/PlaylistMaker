package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {

    var darkTheme = sharedPrefs.getBoolean(DAY_NIGHT_THEME_KEY, false)

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(darkTheme)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {

        if (settings.darkTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPrefs.edit()
                .putBoolean(DAY_NIGHT_THEME_KEY, true)
                .apply()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPrefs.edit()
                .putBoolean(DAY_NIGHT_THEME_KEY, false)
                .apply()
        }
    }

    companion object {
        const val DAY_NIGHT_THEME_KEY = "key"
    }
}