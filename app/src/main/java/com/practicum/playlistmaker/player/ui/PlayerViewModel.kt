package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {

    private var timerJob: Job? = null

    private var _playerStateLiveData = MutableLiveData<PlayerState>()
    fun playerStateLiveData(): LiveData<PlayerState> = _playerStateLiveData

    private var _timerCurrentPositionLiveData = MutableLiveData<String>()
    fun timerCurrentPositionLiveData(): LiveData<String> = _timerCurrentPositionLiveData

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
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format((mediaPlayerInteractor.currentPosition())) ?: "00:00"
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