package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface SearchActivityScreenState {

    object Loading : SearchActivityScreenState

    data class Content(val tracks: List<Track>) : SearchActivityScreenState

    class Error() : SearchActivityScreenState

    class Empty() : SearchActivityScreenState

    data class ContentHistoryList(val historyList: List<Track>) : SearchActivityScreenState

    class EmptyHistoryList() : SearchActivityScreenState

}