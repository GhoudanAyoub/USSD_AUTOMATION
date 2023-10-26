package com.gws.local_models.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Ussds(
    var ussds: List<Ussd>? = null
): Parcelable
