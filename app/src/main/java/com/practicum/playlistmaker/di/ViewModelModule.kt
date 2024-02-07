package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.lib.ui.FavoriteTracksViewModel
import com.practicum.playlistmaker.lib.ui.NewPlaylistCreationViewModel
import com.practicum.playlistmaker.lib.ui.PlaylistInfoViewModel
import com.practicum.playlistmaker.lib.ui.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchActivityViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchActivityViewModel(get())
    }

    viewModel {
        PlayerViewModel(get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoriteTracksViewModel(get(), get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistCreationViewModel(get())
    }

    viewModel {
        PlaylistInfoViewModel(get(), get())
    }
}