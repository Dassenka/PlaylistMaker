package com.practicum.playlistmaker.domain


interface MediaPlayerRepository {

    fun release()
    fun setDataSource(previewUrl: String)
    fun prepareAsync()
    fun setOnPreparedListener(onPreparedListener: () -> Unit)
    fun setOnCompletionListener(onCompletionListener: () -> Unit)
    fun start()
    fun pause()
    fun currentPosition(): Int
}