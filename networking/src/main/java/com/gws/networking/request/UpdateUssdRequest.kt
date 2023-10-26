package com.gws.networking.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUssdRequest(
    val servername: String,
    val dbname: String,
    val username: String,
    val dbpassword: String,
    val ussd_response: String,
    val id: String,
    val etat: String
)
