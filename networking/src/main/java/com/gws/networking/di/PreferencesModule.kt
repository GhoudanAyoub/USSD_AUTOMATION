package com.gws.networking.di

import android.content.Context
import android.content.SharedPreferences
import com.gws.networking.model.SerEntity
import com.gws.networking.model.UserEntity
import com.gws.networking.providers.CurrentServerProvider
import com.gws.networking.providers.CurrentUserProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Named(AUTH_USER_OBJECT)
    @Singleton
    fun provideUserInfoObject(@ApplicationContext context: Context)
            : SharedPreferences {
        return context.getSharedPreferences(
            AUTH_USER_OBJECT,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideCurrentUserInfo(
        json: Json,
        @Named(AUTH_USER_OBJECT) sharedPreferences: SharedPreferences
     ) = object : CurrentUserProvider {
        override suspend fun currentUser(): UserEntity? {

            val userCredentialsJson = sharedPreferences.getString(
                AUTH_USER_OBJECT,
                ""
            )
            val userEntity: UserEntity? = userCredentialsJson?.let {
                 try {
                    json.decodeFromString(
                        UserEntity.serializer(), userCredentialsJson
                    )
                } catch (e: SerializationException) {
                    null
                }
            }

            return userEntity

        }

        override suspend fun saveUser(userEntity: UserEntity) {
            val userEntityJson =
                try {
                    Json.encodeToString(userEntity)
                } catch (e: Exception) {
                    null
                }
            sharedPreferences.edit().putString(AUTH_USER_OBJECT, userEntityJson).commit()
        }

        override suspend fun clearUser() {
            sharedPreferences.edit().clear().commit()
        }
    }

    const val AUTH_USER_OBJECT = "auth.userObject"


    @Provides
    @Named(AUTH_SERVER_OBJECT)
    @Singleton
    fun provideServerInfoObject(@ApplicationContext context: Context)
            : SharedPreferences {
        return context.getSharedPreferences(
            AUTH_SERVER_OBJECT,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideServerInfo(
        json: Json,
        @Named(AUTH_SERVER_OBJECT) sharedPreferences: SharedPreferences
     ) = object : CurrentServerProvider {
        override suspend fun currentServer(): SerEntity? {

            val userCredentialsJson = sharedPreferences.getString(
                AUTH_SERVER_OBJECT,
                ""
            )
            val userEntity: SerEntity? = userCredentialsJson?.let {
                 try {
                    json.decodeFromString(
                        SerEntity.serializer(), userCredentialsJson
                    )
                } catch (e: SerializationException) {
                    null
                }
            }

            return userEntity

        }

        override suspend fun saveServer(serverEntity: SerEntity) {
            val userEntityJson =
                try {
                    Json.encodeToString(serverEntity)
                } catch (e: Exception) {
                    null
                }
            sharedPreferences.edit().putString(AUTH_SERVER_OBJECT, userEntityJson).commit()
        }

        override suspend fun clearServer() {
            sharedPreferences.edit().clear().commit()
        }
    }

    const val AUTH_SERVER_OBJECT = "auth.serverObject"
}
