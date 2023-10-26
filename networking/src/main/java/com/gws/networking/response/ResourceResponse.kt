package com.gws.networking.response

sealed class ResourceResponse <T> (
    var data: T ? = null,
    val error: Throwable ? = null
) {
    class Success<T>(data: T) : ResourceResponse<T>(data)
    class Loading<T>(data: T? = null) : ResourceResponse<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : ResourceResponse<T>(data, throwable)
}
