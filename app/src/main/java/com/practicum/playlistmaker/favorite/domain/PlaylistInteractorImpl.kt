package com.practicum.playlistmaker.favorite.domain

import android.net.Uri
import com.practicum.playlistmaker.favorite.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.favorite.domain.api.PlaylistRepository
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.lib.state.PlaylistInfoState
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun createNewPlaylist(playlist: Playlist) {
        playlistRepository.createNewPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun getPlayListById(playlistId: Int): Playlist {
        return playlistRepository.getPlayListById(playlistId)
    }

    override fun savePlaylistPhotoToPrivateStorage(uri: Uri?): String? {
        return playlistRepository.savePlaylistPhotoToPrivateStorage(uri)
    }

    override suspend fun addTracksInPlaylist(playlistId: Int, trackInPlaylist: Track) {
        playlistRepository.addTracksInPlaylist(playlistId, trackInPlaylist)
    }

    override fun getTracksFromPlaylist(listPlaylistId: List<String>): Flow<List<Track>> {
        return playlistRepository.getTracksFromPlaylist(listPlaylistId)
    }

    override suspend fun deleteTrackFromPlaylist(
        playlistId: String,
        trackId: String
    ): Flow<PlaylistInfoState> {

        val newTrackList = playlistRepository.deleteTrackFromPlaylist(playlistId, trackId)

        if (newTrackList == null) {
            return flow { emit(PlaylistInfoState.Empty) }
        }

        return newTrackList.map { trackList ->
            if (trackList?.isEmpty() == true) {
                PlaylistInfoState.Empty
            } else {
                PlaylistInfoState.updateTracksRecycler(
                    trackList!!.asReversed(),
                )
            }
        }
    }

    override suspend fun deletePlaylistById(playlistId: Int): Flow<PlaylistInfoState> {
        return playlistRepository.deletePlaylistById(playlistId).map {
            when (it) {
                null -> PlaylistInfoState.Error
                else -> PlaylistInfoState.DeletePlaylist
            }
        }
    }
}