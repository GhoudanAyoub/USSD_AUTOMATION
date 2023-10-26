package com.gws.networking.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.gws.networking.repository.UssdRepository
import com.gws.networking.repository.UssdRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUssdRepository(
        ussdRepositoryImpl: UssdRepositoryImpl
    ): UssdRepository
}
