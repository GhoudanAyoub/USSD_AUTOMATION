package com.gws.ussd.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gws.networking.di.PreferencesModule.AUTH_USER_OBJECT
import com.gws.networking.model.UserEntity
import com.gws.networking.providers.CurrentUserProvider
import com.gws.networking.repository.UssdRepository
import com.gws.networking.request.LoginRequest
import com.gws.networking.response.ResourceResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val ussdRepository: UssdRepository,
    var currentUserProvider: CurrentUserProvider
) : ViewModel() {


    private var loginLiveData: MutableLiveData<ResourceResponse<List<UserEntity>>> = MutableLiveData()
    val login: LiveData<ResourceResponse<List<UserEntity>>> = loginLiveData
    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            ussdRepository.login(loginRequest).collect { result ->
                if (result is ResourceResponse.Success) {
                    result.data?.get(0)?.let { currentUserProvider.saveUser(it) }
                }
                loginLiveData.value = result
            }
        }
    }

}
