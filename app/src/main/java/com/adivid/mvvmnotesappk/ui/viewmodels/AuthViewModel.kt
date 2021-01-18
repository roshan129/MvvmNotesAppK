package com.adivid.mvvmnotesappk.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.repositories.AuthRepository
import com.adivid.mvvmnotesappk.repositories.MainRepository
import com.adivid.mvvmnotesappk.ui.fragments.states.UiStates
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    var userCreated = MutableLiveData<Boolean>()
    var progressBar = MutableLiveData<Boolean>()

    var uiStates = MutableLiveData<UiStates>()

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        uiStates.postValue(UiStates.Loading(true))
        //progressBar.postValue(true)
        val b = authRepository.registerUser(email, password)
        userCreated.postValue(b)
        //progressBar.postValue(false)
        uiStates.postValue(UiStates.Loading(false))
    }

}

