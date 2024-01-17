package com.practicum.playlistmaker.favorite.data.db

import com.practicum.playlistmaker.favorite.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.search.domain.model.Track

class TrackInPlaylistDbConvertor {
    fun map(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl)
    }

    fun map(track: TrackInPlaylistEntity): Track {
        return Track(
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl)
    }
}