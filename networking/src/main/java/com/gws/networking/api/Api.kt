package com.gws.networking.api

import com.gws.local_models.models.Ussd
import com.gws.networking.model.UserEntity
import com.gws.networking.request.LoginRequest
import com.gws.networking.request.UpdateUssdRequest
import com.gws.networking.request.UssdRequest
import com.gws.networking.response.Success
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {


    @POST("login.php")
    suspend fun Login(@Body loginRequest: LoginRequest): List<UserEntity>
    @POST("ussd.php")
    suspend fun getUssd(@Body ussdRequest: UssdRequest): List<Ussd>
    @POST("updateSuccessUssd.php")
    suspend fun updateSuccessUssd(@Body updateUssdRequest: UpdateUssdRequest): Success
    @POST("updateFailedUssd.php")
    suspend fun updateFailedUssd(@Body updateUssdRequest: UpdateUssdRequest): Success
}
