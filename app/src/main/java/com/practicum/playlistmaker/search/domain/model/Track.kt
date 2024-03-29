package com.practicum.playlistmaker.search.domain.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale


data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean = false
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    fun getYearFromReleaseDate() = releaseDate.trim().substringBefore("-")

    fun trackTimeFormat(): String? =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
}
