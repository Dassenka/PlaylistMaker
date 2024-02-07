package com.practicum.playlistmaker.lib.state

import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track

sealed interface PlaylistInfoState {

    data class Content(
        val playlist: Playlist
    ) : PlaylistInfoState

    data class fillTracksRecycler(
        val tracks: List<Track>
    ) : PlaylistInfoState

    data class updateTracksRecycler(
        val tracks: List<Track>
    ) : PlaylistInfoState

    object Empty : PlaylistInfoState

    object DeletePlaylist : PlaylistInfoState

    object Error : PlaylistInfoState
}