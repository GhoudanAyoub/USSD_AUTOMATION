package com.gws.ussd.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import com.gws.local_models.models.getConcatUssd
import com.gws.networking.repository.Synchronizer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import timber.log.Timber

@AndroidEntryPoint
class UssdBackgroundService : Service() {

    @Inject
    lateinit var synchronizer: Synchronizer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        synchronizer.createFakeUssdDataList()

        // Sleep for a moment (optional)
        try {
            Thread.sleep(2000) // Sleep for 2 second
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Thread {
            synchronizer?.fakeUssdList?.forEachIndexed { index, item ->
                if (index < synchronizer?.fakeUssdList?.size?.minus(1) ?: 0) {
                    // Execute the USSD request
                    runUssd(this, item.getConcatUssd()) { result, message ->
                        item.reponceussd = message
                        item.etat = if (result) "1" else "0"
                        synchronizer?.updateList(item)
                    }

                    // Sleep for a moment (optional)
                    try {
                        Thread.sleep(5000) // Sleep for 5 second
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun runUssd(context: Context, ussdCode: String, invoke: (Boolean,String) -> Unit) {
        Timber.e("new USSD $ussdCode")
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        try {
            telephonyManager.sendUssdRequest(
                ussdCode,
                object : TelephonyManager.UssdResponseCallback() {
                    override fun onReceiveUssdResponse(
                        telephonyManager: TelephonyManager,
                        request: String,
                        response: CharSequence
                    ) {
                        // Handle the USSD response
                        Timber.e("Sync: response $response")
                        invoke(true,response.toString())
                    }

                    override fun onReceiveUssdResponseFailed(
                        telephonyManager: TelephonyManager,
                        request: String,
                        failureCode: Int
                    ) {
                        // Handle failure
                        Timber.e("Sync: failureCode $failureCode")
                        invoke(false,failureCode.toString() )
                    }
                },
                null
            )

        } catch (e: SecurityException) {
            // Permission denied; request the permission
        }
    }
}
