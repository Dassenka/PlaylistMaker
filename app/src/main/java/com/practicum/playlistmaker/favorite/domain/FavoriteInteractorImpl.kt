package com.practicum.playlistmaker.favorite.domain

import com.practicum.playlistmaker.favorite.domain.api.FavoriteInteractor
import com.practicum.playlistmaker.favorite.domain.api.FavoriteRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository) : FavoriteInteractor {

    override suspend fun addTrackInFavorite(track: Track){
        favoriteRepository.addTrackInFavorite(track)
    }

    override suspend fun deleteTrackFromFavorite(track: Track){
        favoriteRepository.deleteTrackFromFavorite(track)
    }

    override fun allFavoriteTracks(): Flow<List<Track>>{
        return favoriteRepository.allFavoriteTracks()
    }

    override fun getIdFavoriteTrack(): Flow<List<String>>{
        return favoriteRepository.getIdFavoriteTrack()
    }
}