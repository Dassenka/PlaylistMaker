package com.practicum.playlistmaker.favorite.data

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    override fun getTracksFromPlaylist(listPlaylistId: List<String>): Flow<List<Track>> = flow {
        val tracksInAllPlaylists = appDatabase.trackInPlaylistDao().getAllTracksFromPlaylists()

        if (tracksInAllPlaylists == null) {
            emit(emptyList())
        } else {
            val filterTracks = tracksInAllPlaylists.filter { track ->
                listPlaylistId.contains(track.trackId)
            }

            val sortTracks = filterTracks.sortedBy { track ->
                listPlaylistId.indexOf(track.trackId)
            }.reversed()

            if (sortTracks.isEmpty()) {
                emit(emptyList())
            } else {
                emit(convertFromTrackInPlaylistEntity(sortTracks))
            }
        }
    }

    override suspend fun deleteTrackFromPlaylist(
        playlistId: String, trackId: String): Flow<List<Track>?>? {

        try {
            val playlist = getPlayListById(playlistId.toInt())
            val playlistTrackIds = playlist.listOfTracksId as ArrayList<String>

            playlistTrackIds.remove(trackId)
            playlist.numberOfTracks = playlist.numberOfTracks.minus(1)
            updatePlaylist(playlist)

            if (!trackUnusedInAllPlaylist(trackId.toInt())) {
                appDatabase.trackInPlaylistDao().deleteTrackByIdFromTrackInPlaylistTable(trackId)
            }

            val newPlaylist = appDatabase.playlistDao().getPlayListById(playlistId.toInt())
            val newPlaylistTrackIds =
                convertFromStringToList(newPlaylist.listOfTracksId) // FromJson
            val newListTrackIds = newPlaylistTrackIds.map { it.toString() }

            return getTracksFromPlaylist(newListTrackIds)

        } catch (exp: Throwable) {
            return null
        }
    }

    private suspend fun trackUnusedInAllPlaylist(trackId: Int): Boolean {

        val listOfPlaylists = appDatabase.playlistDao().getAllPlaylists()
            .map { playlistEntity ->
                playlistDbConvertor.map(playlistEntity)
            }

        for (playlist in listOfPlaylists) {
            if (!playlist.listOfTracksId.isNullOrEmpty()){
                val listOfTrackId = playlist.listOfTracksId as ArrayList<String>
                val result = listOfTrackId.indexOf(trackId.toString()) > -1
                return result
            }
        }
        return false
    }

    override suspend fun deletePlaylistById(playlistId: Int): Flow<Unit?> {
        try {
            val playlist = appDatabase.playlistDao().getPlayListById(playlistId)
            val listTrackId = convertFromStringToList(playlist.listOfTracksId)  //FromJson

            return if (listTrackId.isEmpty()) {
                appDatabase.playlistDao().deletePlaylistById(playlist.playlistId)
                flow { emit(Unit) }
            } else {
                for (i in 0 until listTrackId.size) {
                    if (!trackUnusedInAllPlaylist(listTrackId[i])) {
                        appDatabase.trackInPlaylistDao()
                            .deleteTrackByIdFromTrackInPlaylistTable(listTrackId[i].toString())
                    }
                }
                appDatabase.playlistDao().deletePlaylistById(playlistId)
                flow { emit(Unit) }
            }
        } catch (ext: Throwable) {
            return flow { emit(null) }
        }
    }

    private fun convertFromStringToList(listIdFromTable: String?): ArrayList<Int> {
        if (!listIdFromTable.isNullOrEmpty()) {
            val itemType = object : TypeToken<ArrayList<Int>>() {}.type
            return Gson().fromJson(listIdFromTable, itemType)
        }
        return ArrayList<Int>()
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