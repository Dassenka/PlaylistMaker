package com.practicum.playlistmaker.favorite.domain

import android.net.Uri
import com.practicum.playlistmaker.favorite.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.favorite.domain.api.PlaylistRepository
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun createNewPlaylist(playlist: Playlist){
        playlistRepository.createNewPlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>>{
        return playlistRepository.getAllPlaylists()
    }

    override fun savePlaylistPhotoToPrivateStorage(uri: Uri?) : String?{
        return  playlistRepository.savePlaylistPhotoToPrivateStorage(uri)
    }

    override suspend fun addTracksInPlaylist(playlistId: Int, trackInPlaylist: Track){
        playlistRepository.addTracksInPlaylist(playlistId, trackInPlaylist)
    }
}