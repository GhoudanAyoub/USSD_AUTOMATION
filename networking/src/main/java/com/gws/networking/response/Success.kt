package com.gws.networking.response

import kotlinx.serialization.Serializable

@Serializable
data class Success(
    val success: Boolean
)
