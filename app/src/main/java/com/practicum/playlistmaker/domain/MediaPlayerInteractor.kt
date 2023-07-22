package com.practicum.playlistmaker.domain

interface MediaPlayerInteractor {

    fun preparePlayer(
        previewUrl: String,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    )

    fun playbackControl(onStartPlayer: () -> Unit, onPausePlayer: () -> Unit)
    fun startPlayer(startPlayer: () -> Unit)
    fun pausePlayer(pausePlayer: () -> Unit)
    fun release()
    fun currentPosition(): Int
}