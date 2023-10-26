package com.gws.ussd.ui.home

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chari.groupewib.com.networking.handler.UssdHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import com.gws.local_models.models.Ussd
import com.gws.networking.providers.CurrentServerProvider
import com.gws.networking.providers.CurrentUserProvider
import com.gws.networking.repository.Synchronizer
import com.gws.networking.repository.UssdRepository
import com.gws.networking.request.UpdateUssdRequest
import com.gws.networking.request.UssdRequest
import com.gws.networking.response.ResourceResponse
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    val synchronizer: Synchronizer,
    val ussdHandler: UssdHandler,
    val ussdRepository: UssdRepository,
    var currentUserProvider: CurrentUserProvider,
    var currentServerProvider: CurrentServerProvider
) : ViewModel() {

    private var UssdLiveData: MutableLiveData<ResourceResponse<List<Ussd>>> = MutableLiveData()
    val ussdList: LiveData<ResourceResponse<List<Ussd>>> = UssdLiveData

    val handler = Handler()
    private val delayMillis = 2500L  // 2.5 seconds

    private val backgroundInfoRunnable = object : Runnable {
        override fun run() {
            getBackgroundInfo()
            handler.postDelayed(this, delayMillis)
        }
    }

    fun startBackgroundInfo() {
        handler.postDelayed(backgroundInfoRunnable, delayMillis)
    }

    fun clearList(){
        ussdHandler.clearUssd()
    }

    /** run background thread function**/
    fun getBackgroundInfo() {
        viewModelScope.launch {
            UssdLiveData.value = ResourceResponse.Loading()
            ussdHandler.getOrCreateUssd().apply {
                if (this.ussds?.isEmpty() == true)
                    UssdLiveData.value = ResourceResponse.Error(Throwable("Error"))
                else this.ussds?.let {
                    Timber.e("Sync: getBackgroundInfo started ${it.size}" +
                            " ${it.filter { it.etat == "1" }.size} "
                    )
                    UssdLiveData.value = ResourceResponse.Success(it)

                }
            }
        }
    }

/**
 * for HomeFragment Only
 * **/
    fun updateList(ussd: Ussd) {
        ussdHandler.addOrUpdateUssd(ussd, resultFunc = {
            viewModelScope.launch {
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
                        UssdLiveData.value?.data?.filter { it.id==ussd.id }
                            ?.map { it.etat = ussd.etat?: "0"  }
                        Timber.e("Ussd: Updated ${ussd.id} ${ussd.etat}")
                    }
                }
            }
        })
    }
    fun getUssdList(){
        viewModelScope.launch {
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
                        UssdLiveData.value = result
                    }
                }
            }
        }
    }
}
