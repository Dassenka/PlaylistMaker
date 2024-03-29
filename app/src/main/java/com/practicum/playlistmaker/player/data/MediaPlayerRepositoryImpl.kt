package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.MediaPlayerRepository


class MediaPlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : MediaPlayerRepository {

    override fun release() {
        mediaPlayer.release()
    }

    override fun setDataSource(previewUrl: String) {

        mediaPlayer.setDataSource(previewUrl)
    }

    override fun prepareAsync() {
        mediaPlayer.prepareAsync()
    }

    override fun setOnPreparedListener(onPreparedListener: () -> Unit) {
        mediaPlayer.setOnPreparedListener {
            onPreparedListener()
        }
    }

    override fun setOnCompletionListener(onCompletionListener: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            onCompletionListener()
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun currentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}


