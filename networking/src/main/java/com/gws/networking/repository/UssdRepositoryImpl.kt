package com.gws.networking.repository

import com.gws.local_models.models.Ussd
import com.gws.networking.api.Api
import com.gws.networking.model.UserEntity
import com.gws.networking.request.LoginRequest
import com.gws.networking.request.UpdateUssdRequest
import com.gws.networking.request.UssdRequest
import com.gws.networking.response.ResourceResponse
import com.gws.networking.response.Success
import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

@Reusable
class UssdRepositoryImpl @Inject constructor(
    private val api: Api
) : UssdRepository {
    override fun login(loginRequest: LoginRequest): Flow<ResourceResponse<List<UserEntity>>> {
        return flow<ResourceResponse<List<UserEntity>>> {
            val result = api.Login(loginRequest)
            emit(ResourceResponse.Success(result))
        }.catch { error ->
            emit(ResourceResponse.Error(error))
        }.onStart { emit(ResourceResponse.Loading()) }
            .flowOn(Dispatchers.IO)
    }

    override fun getUssd(ussdRequest: UssdRequest): Flow<ResourceResponse<List<Ussd>>> {
        return flow<ResourceResponse<List<Ussd>>> {
            val result = api.getUssd(ussdRequest)
            emit(ResourceResponse.Success(result))
        }.catch { error ->
            emit(ResourceResponse.Error(error))
        }.onStart { emit(ResourceResponse.Loading()) }
            .flowOn(Dispatchers.IO)
    }

    override fun updateUssd(updateUssdRequest: UpdateUssdRequest): Flow<ResourceResponse<Success>> {
        return flow<ResourceResponse<Success>> {
            if (updateUssdRequest.etat == "1") {
                val result = api.updateSuccessUssd(updateUssdRequest)
                emit(ResourceResponse.Success(result))
            } else {
                val result = api.updateFailedUssd(updateUssdRequest)
                emit(ResourceResponse.Success(result))
            }
        }.catch { error ->
            emit(ResourceResponse.Error(error))
        }.onStart { emit(ResourceResponse.Loading()) }
            .flowOn(Dispatchers.IO)
    }
}
