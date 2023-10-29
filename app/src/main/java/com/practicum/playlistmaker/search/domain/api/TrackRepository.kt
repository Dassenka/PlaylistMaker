package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.ResponseStatus
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTrack(expression: String): Flow<ResponseStatus<List<Track>>>
    fun getTrackHistoryList(): List<Track>
    fun addTrackInHistory(track: Track)
    fun clearHistory()
}