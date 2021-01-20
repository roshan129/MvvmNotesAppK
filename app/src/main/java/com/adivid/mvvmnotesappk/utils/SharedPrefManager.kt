package com.adivid.mvvmnotesappk.utils

import android.content.Context
import android.content.SharedPreferences
import com.adivid.mvvmnotesappk.utils.Constants.KEY_EMAIL
import javax.inject.Inject

class SharedPrefManager @Inject constructor(
    val context: Context,
    val sharedPreferences: SharedPreferences
) {


    fun saveEmail(email: String) {
        sharedPreferences.edit().apply {
            putString(KEY_EMAIL, email)
                .apply()
        }
    }

    fun getEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "").toString()

    }

    fun clearPrefs() {
        sharedPreferences.edit().clear().apply()
    }

}