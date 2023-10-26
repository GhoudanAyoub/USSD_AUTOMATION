package com.gws.networking.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("page") val page: Int,
    @SerialName("total_results") var totalResults: Int,
    @SerialName("total_pages") var totalPages: Int,
    @SerialName("results") var data: T
)
