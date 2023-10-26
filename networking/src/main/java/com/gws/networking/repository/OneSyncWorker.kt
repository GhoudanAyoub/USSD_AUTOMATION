package com.gws.networking.repository

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random
import kotlinx.coroutines.delay

@HiltWorker
class OneSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val ussdRepository: UssdRepositoryImpl
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        startForegroundService()
        return Result.success()
    }


    private suspend fun startForegroundService() {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, "synchronization_channel")
                    .setContentText("Synchronization...")
                    .setContentTitle("Starting Synchronization")
                    .build()
            )
        )
        delay(1000L)
    }
}
