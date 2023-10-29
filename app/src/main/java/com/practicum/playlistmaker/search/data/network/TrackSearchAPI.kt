package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TrackSearchAPI {
    @GET("/search?entity=song")
    suspend fun searchTrack(@Query("term") text: String): TrackSearchResponse
}