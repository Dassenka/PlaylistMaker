package com.practicum.playlistmaker.favorite.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

import com.practicum.playlistmaker.favorite.data.db.entity.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {

    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(trackInPlaylist: TrackInPlaylistEntity)
}