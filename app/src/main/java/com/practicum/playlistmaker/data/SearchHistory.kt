package com.practicum.playlistmaker.data


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.presentation.App
import com.practicum.playlistmaker.domain.model.Track


const val HISTORY_PREFERENCES = "history_preferences"
const val HISTORY_SIZE = 10


class SearchHistory(private var historyList: ArrayList<Track>) {

    fun addTrackInHistory(track: Track) {
        historyList = createTrackListListFromJson()

        if (historyList.contains(track)) {
            historyList.remove(track)}

        historyList.add(0, track)

        if (historyList.size > HISTORY_SIZE) {
            historyList.removeAt(historyList.size - 1)
        }
        writeHistory(historyList)
    }

    // запись
    private fun writeHistory(trackHistory: ArrayList<Track>) {
        App.sharedPrefs.edit()
            .putString(HISTORY_PREFERENCES, Gson().toJson(trackHistory))
            .apply()
    }

    // чтение
    fun createTrackListListFromJson(): ArrayList<Track> {
        val getHistory = App.sharedPrefs.getString(HISTORY_PREFERENCES, null)
        if (!getHistory.isNullOrEmpty()) {
            val itemType = object : TypeToken<ArrayList<Track>>() {}.type
            historyList = Gson().fromJson(getHistory, itemType)
        }
        return historyList
    }

    fun clearHistory() {
        App.sharedPrefs.edit()
            .remove(HISTORY_PREFERENCES)
            .apply()
    }
}