package com.practicum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivityViewModel(private val trackInteractor: TrackInteractor) : ViewModel() {

    private val _stateLiveData = MutableLiveData<SearchActivityScreenState>()
    fun stateLiveData(): LiveData<SearchActivityScreenState> = _stateLiveData

    private var latestSearchText: String? = null

    private var searchJob: Job? = null

    fun searchDebounce(changedText: String, hasError: Boolean) {
        if (latestSearchText == changedText && !hasError) {
            return
        }

        this.latestSearchText = changedText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchActivityScreenState.Loading)

            viewModelScope.launch {
                trackInteractor
                    .searchTrack(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, hasError: Boolean?) {
        val tracks = mutableListOf<Track>()

        if (foundTracks != null) {
            tracks.addAll(foundTracks)

            when {
                tracks.isEmpty() -> {
                    renderState(SearchActivityScreenState.Empty())
                }
                tracks.isNotEmpty() -> {
                    renderState(SearchActivityScreenState.Content(tracks))
                }
            }
        } else {
            renderState(SearchActivityScreenState.Error())
        }
    }

    fun getHistoryList() {
        val savedTrack = trackInteractor.getTrackHistoryList()
        if (savedTrack != null) {
            when (savedTrack.isEmpty()) {
                true -> {
                    renderState(SearchActivityScreenState.EmptyHistoryList())
                }
                else -> {
                    renderState(SearchActivityScreenState.ContentHistoryList(savedTrack))
                }
            }
        } else {
            renderState(SearchActivityScreenState.EmptyHistoryList())
        }
    }

    fun emptyHistoryList() {
        renderState(SearchActivityScreenState.EmptyHistoryList())
    }

    fun addTrackInHistory(track: Track) {
        trackInteractor.addTrackInHistory(track)
    }

    fun clearHistory() {
        trackInteractor.clearHistory()
    }

    private fun renderState(state: SearchActivityScreenState) {
        _stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}