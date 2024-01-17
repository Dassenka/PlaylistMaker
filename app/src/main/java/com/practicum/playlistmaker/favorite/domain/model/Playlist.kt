package com.practicum.playlistmaker.favorite.domain.model

import java.io.Serializable

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val playlistPhotoPath: String?,
    var listOfTracksId: List<String>?,
    var numberOfTracks: Int,
) : Serializable