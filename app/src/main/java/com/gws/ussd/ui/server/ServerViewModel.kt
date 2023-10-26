package com.gws.ussd.ui.server

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.gws.networking.model.SerEntity
import com.gws.networking.providers.CurrentServerProvider
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ServerViewModel @Inject constructor(
    private var currentServerProvider: CurrentServerProvider
) : ViewModel() {

    fun saveServer(serverEntity: SerEntity){
        viewModelScope.launch {
            currentServerProvider.saveServer(serverEntity)
        }
    }
}
