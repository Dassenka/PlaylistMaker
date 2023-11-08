package com.practicum.playlistmaker.favorite.data

import com.practicum.playlistmaker.favorite.data.db.AppDatabase
import com.practicum.playlistmaker.favorite.data.db.TrackDbConvertor
import com.practicum.playlistmaker.favorite.data.db.entity.TrackEntity
import com.practicum.playlistmaker.favorite.domain.api.FavoriteRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteRepository {

    override suspend fun addTrackInFavorite(track: Track){
        val favTrack = convertToTrackEntity(track)
            appDatabase.trackDao().insertTrack(favTrack)
    }

    override suspend fun deleteTrackFromFavorite(track: Track){
        val favTrack = convertToTrackEntity(track)
        appDatabase.trackDao().deleteTrack(favTrack)
    }

    override fun allFavoriteTracks(): Flow<List<Track>> = flow {
        val convTracks = appDatabase.trackDao().getAllTracks()
        emit(convertFromTrackEntity(convTracks))
    }

    override fun getIdFavoriteTrack(): Flow<List<String>> = flow {
        emit(appDatabase.trackDao().getTracksIds())
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(track: Track): TrackEntity {
        return trackDbConvertor.map(track)
    }
}