package com.adivid.mvvmnotesappk.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adivid.mvvmnotesappk.repositories.AuthRepository
import com.adivid.mvvmnotesappk.ui.fragments.states.LoadingStates
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
): ViewModel() {


    var userCreated = MutableLiveData<Boolean>()
    var progressBar = MutableLiveData<Boolean>()

    var uiStates = MutableLiveData<LoadingStates>()

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        try {
            uiStates.postValue(LoadingStates.Loading(true))
            val b = authRepository.registerUser(email, password)
            userCreated.postValue(b)
            uiStates.postValue(LoadingStates.Loading(false))
        }catch (e: Exception){
            Timber.d("exception: $e")
            uiStates.postValue(LoadingStates.Error(e.message.toString()))
        }

    }

    fun loginUser(email: String, password: String) = viewModelScope.launch{
        try {
            uiStates.postValue(LoadingStates.Loading(true))
            val b = authRepository.loginUser(email, password)
            userCreated.postValue(b)
            uiStates.postValue(LoadingStates.Loading(false))
        }catch (e: Exception){
            uiStates.postValue(LoadingStates.Error(e.message.toString()))
        }

    }

}

