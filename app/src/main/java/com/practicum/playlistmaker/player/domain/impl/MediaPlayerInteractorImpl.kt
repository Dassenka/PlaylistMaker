package com.practicum.playlistmaker.player.domain.impl


import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerRepository
import com.practicum.playlistmaker.player.domain.model.PlayerState


class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository) :
    MediaPlayerInteractor {

    var playerState = PlayerState.STATE_PREPARED

    override fun preparePlayer(
        previewUrl: String,
        ChangePlayerState: (state: PlayerState) -> Unit
    ) {
        mediaPlayerRepository.setDataSource(previewUrl)
        mediaPlayerRepository.prepareAsync()
        mediaPlayerRepository.setOnPreparedListener {
            playerState = PlayerState.STATE_PREPARED
            ChangePlayerState(PlayerState.STATE_PREPARED)
        }
        mediaPlayerRepository.setOnCompletionListener {
            playerState = PlayerState.STATE_PREPARED
            ChangePlayerState(PlayerState.STATE_PREPARED)
        }
    }

    override fun playbackControl(ChangePlayerState: (state: PlayerState) -> Unit) {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
                playerState = PlayerState.STATE_PAUSED
                ChangePlayerState(PlayerState.STATE_PAUSED)

            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
                playerState = PlayerState.STATE_PLAYING
                ChangePlayerState(PlayerState.STATE_PLAYING)
            }
            PlayerState.STATE_DEFAULT -> {}
        }
    }

    override fun startPlayer() {
        mediaPlayerRepository.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pausePlayer() {
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