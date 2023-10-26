package com.gws.networking.repository

import android.content.Context
import androidx.work.WorkManager
import chari.groupewib.com.networking.handler.UssdHandler
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import com.gws.local_models.models.Ussd
import com.gws.networking.providers.CurrentServerProvider
import com.gws.networking.providers.CurrentUserProvider
import com.gws.networking.request.LoginRequest
import com.gws.networking.request.UpdateUssdRequest
import com.gws.networking.request.UssdRequest
import com.gws.networking.response.ResourceResponse
import java.util.Date
import java.util.Random
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Reusable
class Synchronizer @Inject constructor(
    @ApplicationContext val context: Context,
    val ussdHandler: UssdHandler,
    val ussdRepository: UssdRepository,
    var currentUserProvider: CurrentUserProvider,
    var currentServerProvider: CurrentServerProvider
) {

    val fakeUssdList = mutableListOf<Ussd>()

    fun updateList(ussd: Ussd) {
        ussdHandler.addOrUpdateUssd(ussd, resultFunc = {
            CoroutineScope(Dispatchers.IO).launch {
                currentServerProvider.currentServer()?.let { currentServer->
                    currentUserProvider.currentUser()?.let { currentUser ->
                        val updateUssdRequest = UpdateUssdRequest(
                            servername = currentServer.servername,
                            dbname = currentServer.dbname,
                            username = currentServer.username,
                            dbpassword = currentServer.dbpassword,
                            id = ussd.id.toString(),
                            ussd_response = ussd.reponceussd?: "",
                            etat = ussd.etat?:"0",

                        )
                        ussdRepository.updateUssd(updateUssdRequest)
                    }
                }
            }
            Timber.e("Ussd: Updated success")
        })
    }

    fun createFakeUssdDataList(){
        CoroutineScope(Dispatchers.IO).launch {
            currentServerProvider.currentServer()?.let { currentServer->
                currentUserProvider.currentUser()?.let { currentUser->
                    val ussdRequest = UssdRequest(
                        servername = currentServer.servername,
                        dbname = currentServer.dbname,
                        username = currentServer.username,
                        dbpassword = currentServer.dbpassword,
                        userId = currentUser.id,
                        userIdaccount = currentUser.idaccount,
                        userMaxsim1 = currentUser.maxsim1,
                        userSim1 = currentUser.sim1
                    )
                    ussdRepository.getUssd(ussdRequest).collect { result ->
                        if (result is ResourceResponse.Success) {
                            result.data?.let { ussdList ->
                                fakeUssdList.addAll(ussdList)
                            }
                        }
                    }
                }
            }
        }
    }

}
