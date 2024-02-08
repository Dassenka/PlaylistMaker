package com.practicum.playlistmaker.lib.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorite.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.favorite.domain.model.Playlist
import com.practicum.playlistmaker.lib.state.PlaylistInfoState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class PlaylistInfoViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<PlaylistInfoState>()
    fun stateLiveData(): LiveData<PlaylistInfoState> = _stateLiveData

    fun getTracksFromPlaylist(playlist: Playlist) {
        val listOfPlaylistsId = playlist.listOfTracksId?.toList()
        if (listOfPlaylistsId.isNullOrEmpty()) {
            renderPlaylistInfoState(PlaylistInfoState.Empty)
        } else {
            viewModelScope.launch {
                playlistInteractor.getTracksFromPlaylist(listOfPlaylistsId)
                    .collect { track ->
                        renderPlaylistInfoState(PlaylistInfoState.fillTracksRecycler(track))
                    }
            }
        }
    }

    fun deleteTrackFromPlaylist(playlist: Playlist?, trackId: String) {
        val playlistId = playlist?.playlistId.toString()
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistId, trackId).collect {
                renderPlaylistInfoState(it)
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylistById(playlist.playlistId).collect {
                renderPlaylistInfoState(it)
            }
        }
    }

    fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        val message = sharePlaylistMessage(playlist, tracks)
        sharingInteractor.sharePlaylist(message)
    }

    private fun sharePlaylistMessage(playlist: Playlist, tracks: List<Track>): String {
        val sb = StringBuilder()
        sb.append(playlist.playlistName).append("\n")
        if (playlist.playlistDescription?.isNotEmpty() == true) {
            sb.append(playlist.playlistDescription).append("\n")
        }
        val trackWord = formatNumberOfTracks(tracks.size)
        sb.append("$trackWord").append("\n")
        tracks.forEachIndexed { index, track ->
            val trackDuration = track.trackTimeFormat()
            sb.append("${index + 1}. ${track.artistName} - ${track.trackName} ($trackDuration)")
                .append("\n")
        }
        return sb.toString()
    }

    fun formatNumberOfTracks(numberOfTracks: Int): String {
        return when {
            numberOfTracks % 100 in 11..14 -> "$numberOfTracks треков"
            numberOfTracks % 10 == 1 -> "$numberOfTracks трек"
            numberOfTracks % 10 in 2..4 -> "$numberOfTracks трека"
            else -> "$numberOfTracks треков"
        }
    }

    fun getPlaylistTrackTime(tracks: List<Track>): String {
        var playlistTime = 0L
        for (track in tracks) {
            playlistTime += track.trackTimeMillis
        }
        val formatTime = SimpleDateFormat("mm", Locale.getDefault()).format(playlistTime)
        return formatNumberOfMinutes(formatTime.toInt())
    }

    private fun formatNumberOfMinutes(playlistTime: Int): String {
        return when {
            playlistTime % 100 in 11..14 -> "$playlistTime минут"
            playlistTime % 10 == 1 -> "$playlistTime минута"
            playlistTime % 10 in 2..4 -> "$playlistTime минуты"
            else -> "$playlistTime минут"
        }
    }

    private fun renderPlaylistInfoState(state: PlaylistInfoState) {
        _stateLiveData.postValue(state)
    }
}