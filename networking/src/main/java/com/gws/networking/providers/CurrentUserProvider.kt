package com.gws.networking.providers

import com.gws.networking.model.UserEntity
interface CurrentUserProvider {
    fun currentUser(): UserEntity?
    suspend fun saveUser(userEntity: UserEntity)
    suspend fun clearUser()
}
