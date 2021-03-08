package com.adivid.mvvmnotesappk.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adivid.mvvmnotesappk.repositories.AuthRepository
import com.adivid.mvvmnotesappk.ui.fragments.states.LoadingStates
import com.adivid.mvvmnotesappk.utils.SharedPrefManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository,
    private val sharedPrefManager: SharedPrefManager,
) : ViewModel() {

    var userCreated = MutableLiveData<Boolean>()
    var progressBar = MutableLiveData<Boolean>()
    var googleSignIn = MutableLiveData<FirebaseUser>()
    var uiStates = MutableLiveData<LoadingStates>()

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        try {
            uiStates.postValue(LoadingStates.Loading(true))
            val b = authRepository.registerUser(email, password)
            if (b) sharedPrefManager.saveEmail(email)
            userCreated.postValue(b)
            uiStates.postValue(LoadingStates.Loading(false))
        } catch (e: Exception) {
            Timber.d("exception: $e")
            uiStates.postValue(LoadingStates.Error(e.message.toString()))
        }
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        try {
            uiStates.postValue(LoadingStates.Loading(true))
            val b = authRepository.loginUser(email, password)
            userCreated.postValue(b)
            if (b) sharedPrefManager.saveEmail(email)
            uiStates.postValue(LoadingStates.Loading(false))
        } catch (e: Exception) {
            uiStates.postValue(LoadingStates.Error(e.message.toString()))
        }

    }

    fun firebaseAuthWithGoogle(idToken: String) = viewModelScope.launch {
        val user = authRepository.firebaseAuthWithGoogle(idToken)
        if (user != null) sharedPrefManager.saveEmail(user.email ?: "")
        googleSignIn.postValue(user)
        Timber.d("email : ${user!!.email}")
    }

    fun fetchDataFromFirebase() {
        viewModelScope.launch {
            authRepository.fetchDataFromFirebase()
        }
    }



}

