package com.practicum.playlistmaker

import com.practicum.playlistmaker.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.impl.MediaPlayerInteractorImpl

object Creator {
    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(MediaPlayerRepositoryImpl())
    }
}