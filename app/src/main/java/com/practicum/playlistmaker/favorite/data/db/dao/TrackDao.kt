package com.practicum.playlistmaker.favorite.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.favorite.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM fav_tracks_table ORDER BY timeAddToFav DESC")
    suspend fun getAllTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM fav_tracks_table")
    suspend fun getTracksIds(): List<String>
}