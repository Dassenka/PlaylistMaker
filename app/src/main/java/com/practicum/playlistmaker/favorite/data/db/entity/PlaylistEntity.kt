package com.practicum.playlistmaker.favorite.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val playlistPhotoPath: String?,
    val listOfTracksId: String?,
    val numberOfTracks: Int,
)
