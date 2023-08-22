package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.ResponseStatus
import com.practicum.playlistmaker.search.domain.model.Track

interface TrackRepository {
    fun searchTrack(expression: String): ResponseStatus<List<Track>>
    fun getTrackHistoryList(): List<Track>
    fun addTrackInHistory(track: Track)
    fun clearHistory()
}