package com.practicum.playlistmaker.presentation

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(DAY_NIGHT_THEME_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DAY_NIGHT_THEME_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
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
        lateinit var sharedPrefs: SharedPreferences
    }
}