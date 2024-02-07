package com.practicum.playlistmaker.favorite.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.practicum.playlistmaker.favorite.data.db.entity.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {

    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(trackInPlaylist: TrackInPlaylistEntity)

    @Query("SELECT * FROM track_in_playlist_table")
    suspend fun getAllTracksFromPlaylists(): List<TrackInPlaylistEntity>?

    @Query("DELETE FROM track_in_playlist_table WHERE trackId = :id")
    suspend fun deleteTrackByIdFromTrackInPlaylistTable(id: String)
}