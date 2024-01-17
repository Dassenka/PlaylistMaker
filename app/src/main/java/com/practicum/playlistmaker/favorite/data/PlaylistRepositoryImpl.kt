package com.practicum.playlistmaker.favorite.data

import android.net.Uri
import android.util.Log
import com.practicum.playlistmaker.favorite.data.db.AppDatabase
import com.practicum.playlistmaker.favorite.data.db.PlaylistDbConvertor
import com.practicum.playlistmaker.favorite.data.db.TrackInPlaylistDbConvertor
import com.practicum.playlistmaker.favorite.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.favorite.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.favorite.domain.api.PlaylistRepository
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackInPlaylistDbConvertor: TrackInPlaylistDbConvertor,
    private val playlistPhotoStorage: PlaylistPhotoStorage,
) : PlaylistRepository {

    override suspend fun createNewPlaylist(playlist: Playlist) {
        val newPlaylist = convertToPlaylistEntity(playlist)
        appDatabase.playlistDao().insertPlaylist(newPlaylist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val newPlaylist = convertToPlaylistEntity(playlist)
        appDatabase.playlistDao().updatePlaylist(newPlaylist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val allPlaylists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(allPlaylists))
    }

    override suspend fun getPlayListById(playlistId: Int): Playlist {
        val playlist = appDatabase.playlistDao().getPlayListById(playlistId)
        return playlistDbConvertor.map(playlist)
    }

    override fun savePlaylistPhotoToPrivateStorage(uri: Uri?): String? {
        return playlistPhotoStorage.savePlaylistPhotoToPrivateStorage(uri)
    }

    override suspend fun addTracksInPlaylist(playlistId: Int, trackInPlaylist: Track) {
        val addedTrack = convertToTrackInPlaylistEntity(trackInPlaylist)
        appDatabase.trackInPlaylistDao().insertTrackInPlaylist(addedTrack)
        val playlistForAddedTrack = getPlayListById(playlistId)
        playlistForAddedTrack.listOfTracksId =
            playlistForAddedTrack.listOfTracksId?.plus(trackInPlaylist.trackId)
        val countNumberOfTracksPlusOne = 1
        playlistForAddedTrack.numberOfTracks =
            playlistForAddedTrack.numberOfTracks + countNumberOfTracksPlusOne
        updatePlaylist(playlistForAddedTrack)
    }

    private fun convertFromPlaylistEntity(playList: List<PlaylistEntity>): List<Playlist> {
        return playList.map { playList -> playlistDbConvertor.map(playList) }
    }

    private fun convertToPlaylistEntity(playList: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playList)
    }

    private fun convertFromTrackInPlaylistEntity(tracks: List<TrackInPlaylistEntity>): List<Track> {
        return tracks.map { track -> trackInPlaylistDbConvertor.map(track) }
    }

    private fun convertToTrackInPlaylistEntity(track: Track): TrackInPlaylistEntity {
        return trackInPlaylistDbConvertor.map(track)
    }
}