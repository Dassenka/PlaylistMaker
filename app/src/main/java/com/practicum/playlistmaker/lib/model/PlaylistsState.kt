package com.practicum.playlistmaker.lib.model

import com.practicum.playlistmaker.favorite.domain.model.Playlist


sealed interface PlaylistsState{
    data class Content(
        val playlist: List<Playlist>
    ) : PlaylistsState

    class Empty : PlaylistsState
}