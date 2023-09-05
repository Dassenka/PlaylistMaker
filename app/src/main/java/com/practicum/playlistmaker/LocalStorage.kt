package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.dto.TrackDto


class LocalStorage(private val sharedPreferences: SharedPreferences) {

    fun getTrackHistoryList(): ArrayList<TrackDto> {
        val getHistory = sharedPreferences.getString(HISTORY_PREFERENCES, null)
        if (!getHistory.isNullOrEmpty()) {
            val itemType = object : TypeToken<ArrayList<TrackDto>>() {}.type
            return Gson().fromJson(getHistory, itemType)
        }
        return ArrayList<TrackDto>()
    }

    fun addTrackInHistory(trackHistory: ArrayList<TrackDto>) {
        sharedPreferences.edit()
            .putString(HISTORY_PREFERENCES, Gson().toJson(trackHistory))
            .apply()
    }

    fun clearHistory() {
        sharedPreferences.edit()
            .remove(HISTORY_PREFERENCES)
            .apply()
    }

    private companion object {
        const val HISTORY_PREFERENCES = "history_preferences"
    }
}