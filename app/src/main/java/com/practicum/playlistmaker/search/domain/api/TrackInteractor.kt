package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTrack(expression: String) : Flow<Pair<List<Track>?, Boolean?>>
    fun getTrackHistoryList(): List<Track>
    fun addTrackInHistory(track: Track)
    fun clearHistory()
}