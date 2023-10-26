package com.gws.networking.repository

import com.gws.local_models.models.Ussd
import com.gws.networking.model.UserEntity
import com.gws.networking.request.LoginRequest
import com.gws.networking.request.UpdateUssdRequest
import com.gws.networking.request.UssdRequest
import com.gws.networking.response.ResourceResponse
import com.gws.networking.response.Success
import kotlinx.coroutines.flow.Flow

interface UssdRepository {
    fun login(loginRequest: LoginRequest): Flow<ResourceResponse<List<UserEntity>>>
    fun getUssd(ussdRequest: UssdRequest): Flow<ResourceResponse<List<Ussd>>>
    fun updateUssd(updateUssdRequest: UpdateUssdRequest): Flow<ResourceResponse<Success>>
}
