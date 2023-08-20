package com.practicum.playlistmaker.player.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.model.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()

    // Создаём Handler, привязанный к ГЛАВНОМУ потоку для отсчета времени
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var timer = createUpdateTimerTask()

    private var playerStateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var timerCurrentPositionLiveData = MutableLiveData<String>()
    fun getTimerCurrentPosition(): LiveData<String> = timerCurrentPositionLiveData


    // функция для подготовки медиаплеера
    fun preparePlayer(previewUrl: String) {
        mediaPlayerInteractor.preparePlayer(previewUrl) { state ->
            when (state) {
                PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                    playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
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
                mainThreadHandler.postDelayed(this, DELAY)
                timerCurrentPositionLiveData.postValue(currentPosition)
            }
        }
    }

    fun playerState() {
        mediaPlayerInteractor.playbackControl { state ->
            when (state) {
                PlayerState.STATE_PREPARED, PlayerState.STATE_DEFAULT -> {
                    timerPause()
                    timerStart()
                    playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
                }
                PlayerState.STATE_PLAYING -> {
                    timerStart()
                    playerStateLiveData.postValue(PlayerState.STATE_PLAYING)
                }
                PlayerState.STATE_PAUSED -> {
                    timerPause()
                    playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
                }
            }
        }
    }

    fun onPause() {
        timerPause()
        mediaPlayerInteractor.pausePlayer()
        playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
    }

    fun onResume() {
        timerPause()
        playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
    }

    fun onDestroy() {
        timerPause()
        mediaPlayerInteractor.release()
    }

    companion object {
        private const val DELAY = 500L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}