package com.practicum.playlistmaker.lib.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorite.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistCreationViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun saveNewPlayList(playlist: Playlist) {

        viewModelScope.launch {
            playlistInteractor
                .createNewPlaylist(playlist)
        }
    }

    fun savePlaylistPhotoToPrivateStorage(uri: Uri?): String? {
        return playlistInteractor.savePlaylistPhotoToPrivateStorage(uri)

    }
}
