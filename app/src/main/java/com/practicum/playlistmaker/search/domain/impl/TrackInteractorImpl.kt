package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.ResponseStatus
import com.practicum.playlistmaker.search.data.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.model.Track
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(expression: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            when(val resource = repository.searchTrack(expression)) {
                is ResponseStatus.Success -> { consumer.consume(resource.data, false) }
                is ResponseStatus.Error -> { consumer.consume(null,  true) }
            }
        }
    }

    override fun getTrackHistoryList(consumer: TrackInteractor.HistoryTrackConsumer){
        consumer.consume(repository.getTrackHistoryList())
    }

    override fun addTrackInHistory(track: Track){
        repository.addTrackInHistory(track)
    }

    override fun clearHistory(){
        repository.clearHistory()
    }
}