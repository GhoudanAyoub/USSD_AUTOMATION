package com.gws.networking.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.moczul.ok2curl.CurlInterceptor
import com.moczul.ok2curl.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.gws.networking.api.Api
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    @Singleton
    @Provides
    fun providesOkHttp(
    ): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(CurlInterceptor(object : Logger {
                override fun log(message: String) {
                    Timber.v("Curl", message)
                }
            }))
            .build()

    }

    @Singleton
    @Provides
    fun provideJsonSerializer(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            explicitNulls = false
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    fun providesApi(httpClient: OkHttpClient, jsonSerializer: Json): Api {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(com.gws.common.utils.Constant.API_URL)
            .addConverterFactory(jsonSerializer.asConverterFactory(contentType))
            .client(httpClient)
            .build()
            .create(Api::class.java)
    }
}
