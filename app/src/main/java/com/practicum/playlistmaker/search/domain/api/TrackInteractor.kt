package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface TrackInteractor {
    fun searchTrack(expression: String, consumer: TrackConsumer)
    fun getTrackHistoryList(consumer: HistoryTrackConsumer)
    fun addTrackInHistory(track: Track)
    fun clearHistory()

    interface TrackConsumer {
        fun consume(foundTrack: List<Track>?, hasError: Boolean?)
    }

    interface HistoryTrackConsumer {
        fun consume(savedTrack: List<Track>?)
    }
}