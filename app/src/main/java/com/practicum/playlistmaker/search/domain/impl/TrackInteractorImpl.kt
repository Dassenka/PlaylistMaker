package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.ResponseStatus
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun searchTrack(expression: String): Flow<Pair<List<Track>?, Boolean?>> {
        return repository.searchTrack(expression).map { result ->
            when (result) {
                is ResponseStatus.Success -> {
                    Pair(result.data, false)
                }
                is ResponseStatus.Error -> {
                    Pair(null, true)
                }
            }
        }
    }

    override fun getTrackHistoryList(): List<Track>{
        return repository.getTrackHistoryList()
    }

    override fun addTrackInHistory(track: Track) {
        repository.addTrackInHistory(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}