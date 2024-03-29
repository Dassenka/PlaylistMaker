package com.practicum.playlistmaker.search.data


import com.practicum.playlistmaker.LocalStorage
import com.practicum.playlistmaker.ResponseStatus
import com.practicum.playlistmaker.favorite.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val HISTORY_SIZE = 10

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localStorage: LocalStorage,
    private val appDatabase: AppDatabase,
) : TrackRepository {


    override fun searchTrack(expression: String): Flow<ResponseStatus<List<Track>>> = flow {

        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(ResponseStatus.Error(true))
            }


            200 -> {
                with(response as TrackSearchResponse) {
                    val data = results.map {
                        Track(
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.trackId,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl,
                        )
                    }
                    val idFavoriteTrack = appDatabase.trackDao().getTracksIds()
                    favoriteClicked(data, idFavoriteTrack)
                    emit(ResponseStatus.Success(data))
                }
            }
            else -> {
                emit(ResponseStatus.Error(true))

            }
        }
    }

    override fun getTrackHistoryList(): List<Track> {
        var historyList = localStorage.getTrackHistoryList().map {
            Track(
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.trackId,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl,
            )
        }
        return historyList
    }

    override fun addTrackInHistory(track: Track) {
        var trackDto = TrackDto(
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )

        var historyListDto: ArrayList<TrackDto> = localStorage.getTrackHistoryList()

        if (historyListDto.contains(trackDto)) {
            historyListDto.remove(trackDto)
        }

        historyListDto.add(0, trackDto)

        if (historyListDto.size > HISTORY_SIZE) {
            historyListDto.removeAt(historyListDto.size - 1)
        }
        localStorage.addTrackInHistory(historyListDto)
    }

    override fun clearHistory() {
        localStorage.clearHistory()
    }

    private fun favoriteClicked(data: List<Track>, idFavoriteTrack: List<String>) {
        for (track in data) {
            if (idFavoriteTrack.contains(track.trackId)) {
                track.isFavorite = true
            }
        }
    }
}