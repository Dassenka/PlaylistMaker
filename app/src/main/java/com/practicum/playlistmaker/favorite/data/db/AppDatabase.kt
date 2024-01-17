package com.practicum.playlistmaker.favorite.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.favorite.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.favorite.data.db.dao.TrackDao
import com.practicum.playlistmaker.favorite.data.db.dao.TrackInPlaylistDao
import com.practicum.playlistmaker.favorite.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.favorite.data.db.entity.TrackEntity
import com.practicum.playlistmaker.favorite.data.db.entity.TrackInPlaylistEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao

}