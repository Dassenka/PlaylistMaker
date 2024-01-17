package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.practicum.playlistmaker.favorite.data.FavoriteRepositoryImpl
import com.practicum.playlistmaker.favorite.data.PlaylistRepositoryImpl
import com.practicum.playlistmaker.favorite.data.db.PlaylistDbConvertor
import com.practicum.playlistmaker.favorite.data.db.TrackDbConvertor
import com.practicum.playlistmaker.favorite.data.db.TrackInPlaylistDbConvertor
import com.practicum.playlistmaker.favorite.domain.api.FavoriteRepository
import com.practicum.playlistmaker.favorite.domain.api.PlaylistRepository
import com.practicum.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.MediaPlayerRepository
import com.practicum.playlistmaker.search.data.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<TrackRepository> {
        TrackRepositoryImpl(get(), get(), get())
    }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

    factory {
        MediaPlayer()
    }

    factory {
        androidContext()
            .getSharedPreferences(
                SettingsRepositoryImpl.DAY_NIGHT_THEME_KEY,
                Context.MODE_PRIVATE
            )
    }

    factory { TrackDbConvertor() }

    factory { PlaylistDbConvertor() }

    factory { TrackInPlaylistDbConvertor() }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get(), get())
    }
}