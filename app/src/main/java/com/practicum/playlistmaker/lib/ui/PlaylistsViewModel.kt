package com.practicum.playlistmaker.lib.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorite.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.lib.state.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<PlaylistsState>()
    fun stateLiveData(): LiveData<PlaylistsState> = _stateLiveData

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getAllPlaylists()
                .collect { playlist ->
                    processResult(playlist)
                }
        }
    }

    private fun processResult(playlist: List<Playlist>) {
        if (playlist.isEmpty()) {
            renderState(PlaylistsState.Empty())
        } else {
            renderState(PlaylistsState.Content(playlist))
        }
    }

    private fun renderState(state: PlaylistsState) {
        _stateLiveData.postValue(state)
    }
}