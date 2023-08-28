package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.model.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {

    // Создаём Handler, привязанный к ГЛАВНОМУ потоку для отсчета времени
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var timer = createUpdateTimerTask()

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

    private fun timerStart() {
        mainThreadHandler.post(timer)
    }

    private fun timerPause() {
        mainThreadHandler.removeCallbacks(timer)
    }

    //таймер проигрывателя
    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                var currentPosition = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format((mediaPlayerInteractor.currentPosition()))
                mainThreadHandler.postDelayed(this, DELAY_MS)
                _timerCurrentPositionLiveData.postValue(currentPosition)
            }
        }
    }

    fun playerState() {
        mediaPlayerInteractor.playbackControl { state ->
            when (state) {
                PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                    timerPause()
                    timerStart()
                    _playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
                }
                PlayerState.STATE_PLAYING -> {
                    timerStart()
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
        private const val DELAY_MS = 500L
    }
}