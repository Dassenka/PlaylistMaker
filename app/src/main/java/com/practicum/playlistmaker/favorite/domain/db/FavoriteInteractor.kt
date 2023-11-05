package com.practicum.playlistmaker.favorite.domain.db

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    suspend fun addTrackInFavorite(track: Track)
    suspend fun deleteTrackFromFavorite(track: Track)
    fun allFavoriteTracks(): Flow<List<Track>>
    fun getIdFavoriteTrack(): Flow<List<String>>
}