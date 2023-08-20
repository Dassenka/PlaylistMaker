package com.practicum.playlistmaker.search.data.dto


data class TrackSearchResponse (val searchType: String,
                                val expression: String,
                                val results: ArrayList<TrackDto>) : Response()