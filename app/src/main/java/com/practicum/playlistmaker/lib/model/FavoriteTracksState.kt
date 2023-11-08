package com.practicum.playlistmaker.lib.model

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface FavoriteTracksState{

    data class Content(
        val tracks: List<Track>
    ) : FavoriteTracksState

    data class Empty(
        val message: String
    ) : FavoriteTracksState
}