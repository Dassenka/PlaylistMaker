package com.practicum.playlistmaker.player.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorite.domain.api.FavoriteInteractor
import com.practicum.playlistmaker.favorite.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.lib.model.PlaylistsState
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null

    private var _playerStateLiveData = MutableLiveData<PlayerState>()
    fun playerStateLiveData(): LiveData<PlayerState> = _playerStateLiveData

    private var _timerCurrentPositionLiveData = MutableLiveData<String>()
    fun timerCurrentPositionLiveData(): LiveData<String> = _timerCurrentPositionLiveData

    private var _isFavoriteLiveData = MutableLiveData<Boolean>()
    fun isFavoriteLiveData(): LiveData<Boolean> = _isFavoriteLiveData

    private val _statePlaylistsLiveData = MutableLiveData<PlaylistsState>()
    fun statePlaylistsLiveData(): LiveData<PlaylistsState> = _statePlaylistsLiveData

    private var _isTrackInPlaylistLiveData = MutableLiveData<Boolean>()
    fun isTrackInPlaylistLiveData(): LiveData<Boolean> = _isTrackInPlaylistLiveData

    // функция для подготовки медиаплеера
    fun preparePlayer(previewUrl: String) {
        mediaPlayerInteractor.preparePlayer(previewUrl) { state ->
            when (state) {
                PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                    _playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
                    timerPause()
                }
                else -> {}
            }
        }
    }

    private fun timerStart(state: PlayerState) {
        timerJob = viewModelScope.launch {
            while (state == PlayerState.STATE_PLAYING) {
                delay(DELAY_MS)
                _timerCurrentPositionLiveData.postValue(currentPosition())
            }
        }
    }

    private fun timerPause() {
        timerJob?.cancel()
    }

    //таймер проигрывателя
    private fun currentPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format((mediaPlayerInteractor.currentPosition())) ?: "00:00"
    }

    fun playerState() {
        mediaPlayerInteractor.playbackControl { state ->
            when (state) {
                PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                    timerPause()
                    timerStart(PlayerState.STATE_PLAYING)
                    _playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
                }
                PlayerState.STATE_PLAYING -> {
                    timerStart(PlayerState.STATE_PLAYING)
                    _playerStateLiveData.postValue(PlayerState.STATE_PLAYING)
                }
                PlayerState.STATE_PAUSED -> {
                    timerPause()
                    _playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
                }
            }
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (track.isFavorite) {
                favoriteInteractor.deleteTrackFromFavorite(track)
                _isFavoriteLiveData.postValue(false)
                track.isFavorite = false
            } else {
                favoriteInteractor.addTrackInFavorite(track)
                _isFavoriteLiveData.postValue(true)
                track.isFavorite = true
            }
        }
    }

    fun checkIdFavoriteTrack(track: Track) {
        viewModelScope.launch {
            val favoriteTracksId: Flow<List<String>> = favoriteInteractor.getIdFavoriteTrack()

            val _favoriteTracksId: MutableList<String> = mutableListOf()

            favoriteTracksId.collect { list ->
                _favoriteTracksId.addAll(list)
            }
            if (_favoriteTracksId.contains(track.trackId)) {
                _isFavoriteLiveData.postValue(true)
                track.isFavorite = true
            } else {
                _isFavoriteLiveData.postValue(false)
                track.isFavorite = false
            }
        }
    }

    fun addTrackInPlaylist(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            val tracksIdInPlaylist = playlist.listOfTracksId.toString()

            if (tracksIdInPlaylist.contains(track.trackId)) {
                _isTrackInPlaylistLiveData.postValue(false)

            } else {
                playlistInteractor.addTracksInPlaylist(playlist.playlistId, track)
                fillData()
                _isTrackInPlaylistLiveData.postValue(true)
            }
        }
    }

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
            renderPlaylistsState(PlaylistsState.Empty())
        } else {
            renderPlaylistsState(PlaylistsState.Content(playlist))
        }
    }

    private fun renderPlaylistsState(state: PlaylistsState) {
        _statePlaylistsLiveData.postValue(state)
    }

    fun onPause() {
        timerPause()
        mediaPlayerInteractor.pausePlayer()
        _playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
    }

    fun onResume() {
        timerPause()
        _playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
    }

    fun onDestroy() {
        timerPause()
        mediaPlayerInteractor.release()
    }

    companion object {
        private const val DELAY_MS = 300L
    }
}