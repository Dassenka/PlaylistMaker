package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.model.PlayerState


interface MediaPlayerInteractor {

    fun preparePlayer(
        previewUrl: String,
        ChangePlayerState: (state: PlayerState) -> Unit
    )

    fun playbackControl(ChangePlayerState: (state: PlayerState) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun release()
    fun currentPosition(): Int
}