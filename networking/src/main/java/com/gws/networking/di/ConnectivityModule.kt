package com.gws.networking.di

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object ConnectivityModule {

    @Provides
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ) = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    fun provideTelephonyManager(
        @ApplicationContext context: Context
    ) = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
}
