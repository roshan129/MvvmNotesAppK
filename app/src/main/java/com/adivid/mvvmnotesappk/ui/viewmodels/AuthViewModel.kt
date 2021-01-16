package com.adivid.mvvmnotesappk.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.repositories.AuthRepository
import com.adivid.mvvmnotesappk.repositories.MainRepository
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    var userCreated = MutableLiveData<Boolean>()
    var progressBar = MutableLiveData<Boolean>()

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        progressBar.postValue(true)
        val b = authRepository.registerUser(email, password)
        userCreated.postValue(b)
        progressBar.postValue(false)
    }

}

