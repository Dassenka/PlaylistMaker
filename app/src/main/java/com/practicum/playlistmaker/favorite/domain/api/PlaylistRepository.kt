package com.practicum.playlistmaker.favorite.domain.api


import android.net.Uri
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow


interface PlaylistRepository {
    suspend fun createNewPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlayListById(playlistId: Int): Playlist
    fun savePlaylistPhotoToPrivateStorage(uri: Uri?): String?

    suspend fun addTracksInPlaylist(playlistId: Int, trackInPlaylist: Track)
}