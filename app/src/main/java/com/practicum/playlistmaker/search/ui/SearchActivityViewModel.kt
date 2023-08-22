package com.practicum.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.TrackInteractor
import com.practicum.playlistmaker.search.domain.model.Track

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val trackInteractor = Creator.provideTrackInteractor(getApplication())
    private val handler = Handler(Looper.getMainLooper())


    private val _stateLiveData = MutableLiveData<SearchActivityScreenState>()
    fun stateLiveData(): LiveData<SearchActivityScreenState> = _stateLiveData

    private var latestSearchText: String? = null

    //onDestroy
    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String, hasError :Boolean) {
        if (latestSearchText == changedText && !hasError) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchActivityScreenState.Loading)

            trackInteractor.searchTrack(newSearchText, object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?, hasError: Boolean?) {
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
                    }else{
                        renderState(SearchActivityScreenState.Error())
                    }
                }
            })
        }
    }

    fun getHistoryList(){
        trackInteractor.getTrackHistoryList(object : TrackInteractor.HistoryTrackConsumer{
            override fun consume(savedTrack: List<Track>?){
                if (savedTrack != null){
                    renderState(SearchActivityScreenState.ContentHistoryList(savedTrack))
                }else{
                    renderState(SearchActivityScreenState.EmptyHistoryList())
                }
            }
        })
    }

    fun addTrackInHistory(track: Track){
        trackInteractor.addTrackInHistory(track)
    }

    fun clearHistory(){
        trackInteractor.clearHistory()
    }


    private fun renderState(state: SearchActivityScreenState) {
        _stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchActivityViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }
}