package com.adivid.mvvmnotesappk.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    suspend fun registerUser(email: String, password: String): Boolean {
        auth.createUserWithEmailAndPassword(email, password).await()
        return auth.currentUser != null
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        auth.signInWithEmailAndPassword(email, password).await()
        return auth.currentUser != null
    }


}