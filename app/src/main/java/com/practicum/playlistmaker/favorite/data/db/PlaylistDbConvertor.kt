package com.practicum.playlistmaker.favorite.data.db

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.favorite.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.favorite.domain.model.Playlist


class PlaylistDbConvertor {

    fun map(playList: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playList.playlistId,
            playList.playlistName,
            playList.playlistDescription,
            playList.playlistPhotoPath,
            convertFromListToString(playList.listOfTracksId),
            playList.numberOfTracks,
        )
    }

    fun map(playList: PlaylistEntity): Playlist {
        return Playlist(
            playList.playlistId,
            playList.playlistName,
            playList.playlistDescription,
            playList.playlistPhotoPath,
            convertFromStringToList(playList.listOfTracksId),
            playList.numberOfTracks,
        )
    }

    private fun convertFromListToString(listId: List<String>?): String? {
        if (!listId.isNullOrEmpty()) {
            return Gson().toJson(listId)
        }
        return ""
    }

    private fun convertFromStringToList(listIdFromTable: String?): List<String>? {
        if (!listIdFromTable.isNullOrEmpty()) {
            val itemType = object : TypeToken<List<String>>() {}.type
            return Gson().fromJson(listIdFromTable, itemType)
        }
        return emptyList()
    }
}