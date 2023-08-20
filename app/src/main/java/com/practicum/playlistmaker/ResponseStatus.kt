package com.practicum.playlistmaker

sealed class ResponseStatus<T>(val data: T? = null, val hasError: Boolean? = false) {
    class Success<T>(data: T): ResponseStatus<T>(data)
    class Error<T>(hasError: Boolean?, data: T? = null): ResponseStatus<T>(data, hasError)
}
