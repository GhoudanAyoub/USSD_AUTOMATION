package com.gws.networking.request

import kotlinx.serialization.Serializable

@Serializable
data class UssdRequest(
    val servername: String,
    val dbname: String,
    val username : String,
    val dbpassword : String,
    val userId: String,
    val userSim1: String,
    val userIdaccount: String,
    val userMaxsim1: String
)
