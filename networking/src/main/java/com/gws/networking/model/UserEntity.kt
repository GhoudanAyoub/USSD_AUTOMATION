package com.gws.networking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UserEntity(
    val id: String,
    val idaccount: String,
    val sim1: String,
    val sim2: String,
    val maxsim1: String,
    val maxsim2: String,
    val login: String,
    val password: String,
    val etat: String,
    val refresh: String,
    val idlogin: String,
    val datesaisie: String

):Parcelable
