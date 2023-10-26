package com.gws.networking.providers

import com.gws.networking.model.SerEntity

interface CurrentServerProvider {
    suspend fun currentServer(): SerEntity?
    suspend fun saveServer(serverEntity: SerEntity)
    suspend fun clearServer()
}
