package com.practicum.playlistmaker.lib.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.favorite.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.lib.model.FavoriteTracksState
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val context: Context,
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<FavoriteTracksState>()
    fun stateLiveData(): LiveData<FavoriteTracksState> = _stateLiveData

    fun fillData() {
        viewModelScope.launch {
            favoriteInteractor
                .allFavoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteTracksState.Empty(context.getString(R.string.empty_favorite_tracks)))
        } else {
            renderState(FavoriteTracksState.Content(tracks))
        }
    }

    private fun renderState(state: FavoriteTracksState) {
        _stateLiveData.postValue(state)
    }
}