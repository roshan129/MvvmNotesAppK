package com.adivid.mvvmnotesappk.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adivid.mvvmnotesappk.repositories.AuthRepository
import com.adivid.mvvmnotesappk.utils.NetworkResponse
import com.adivid.mvvmnotesappk.utils.SharedPrefManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository,
    private val sharedPrefManager: SharedPrefManager,
) : ViewModel() {

    var googleSignIn = MutableLiveData<FirebaseUser>()
    var userCreated1 = MutableLiveData<NetworkResponse<Boolean>>()

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        try {
            userCreated1.postValue(NetworkResponse.Loading())
            val b = authRepository.registerUser(email, password)
            if (b) sharedPrefManager.saveEmail(email)
            userCreated1.postValue(NetworkResponse.Success(b))
        } catch (e: Exception) {
            Timber.d("exception: $e")
            userCreated1.postValue(NetworkResponse.Error(e.message.toString()))
        }
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        try {
            userCreated1.postValue(NetworkResponse.Loading())
            val b = authRepository.loginUser(email, password)
            Timber.d("booleans: %s", b)
            userCreated1.postValue(NetworkResponse.Success(b))
            if (b) sharedPrefManager.saveEmail(email)
            //userCreated1.postValue(NetworkResponse.Loading())
        } catch (e: Exception) {
            userCreated1.postValue(NetworkResponse.Error(e.message.toString()))
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) = viewModelScope.launch {
        val user = authRepository.firebaseAuthWithGoogle(idToken)
        if (user != null) sharedPrefManager.saveEmail(user.email ?: "")
        googleSignIn.postValue(user!!)
    }

    fun fetchDataFromFirebase() {
        viewModelScope.launch {
            authRepository.fetchDataFromFirebase()
        }
    }



}

