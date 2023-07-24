package com.practicum.playlistmaker.domain.impl


import com.practicum.playlistmaker.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.MediaPlayerRepository
import com.practicum.playlistmaker.domain.model.PlayerState


class MediaPlayerInteractorImpl(val mediaPlayerRepository: MediaPlayerRepository) :
    MediaPlayerInteractor {

    var playerState = PlayerState.STATE_DEFAULT

    override fun preparePlayer(
        previewUrl: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        mediaPlayerRepository.setDataSource(previewUrl)
        mediaPlayerRepository.prepareAsync()
        mediaPlayerRepository.setOnPreparedListener {
            onPreparedListener()
            playerState = PlayerState.STATE_PREPARED
        }
        mediaPlayerRepository.setOnCompletionListener {
            onCompletionListener()
            playerState = PlayerState.STATE_PREPARED
        }
    }

    override fun playbackControl(onStartPlayer: () -> Unit, onPausePlayer: () -> Unit) {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                onPausePlayer()
                pausePlayer(onPausePlayer)
                playerState = PlayerState.STATE_PAUSED

            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                onStartPlayer()
                startPlayer(onStartPlayer)
                playerState = PlayerState.STATE_PLAYING
            }
            PlayerState.STATE_DEFAULT -> {}
        }
    }

    override fun startPlayer(startPlayer: () -> Unit) {
        startPlayer()
        mediaPlayerRepository.start()
        playerState = PlayerState.STATE_PLAYING

    }

    override fun pausePlayer(pausePlayer: () -> Unit) {
        pausePlayer()
        mediaPlayerRepository.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun release() {
        mediaPlayerRepository.release()
    }

    override fun currentPosition(): Int {
        return mediaPlayerRepository.currentPosition()
    }
}