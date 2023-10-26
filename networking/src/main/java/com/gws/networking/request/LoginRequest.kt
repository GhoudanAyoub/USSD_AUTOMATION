package com.gws.networking.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val servername: String,
    val dbname: String,
    val username : String,
    val dbpassword : String,
    val login : String,
    val password : String
)
