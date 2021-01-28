package com.adivid.mvvmnotesappk.utils

import android.content.Context
import android.content.SharedPreferences
import com.adivid.mvvmnotesappk.utils.Constants.KEY_EMAIL
import com.adivid.mvvmnotesappk.utils.Constants.KEY_NIGHT_MODE
import com.adivid.mvvmnotesappk.utils.Constants.KEY_TRANSFER_DATA
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

    fun isNightModeOn(): Boolean {
        return sharedPreferences.getBoolean(KEY_NIGHT_MODE, false)
    }

    fun saveNightMode(b: Boolean){
        sharedPreferences.edit().apply{
            putBoolean(KEY_NIGHT_MODE, b).apply()
        }
    }

    fun showTransferDialogPref(b: Boolean){
        sharedPreferences.edit().apply{
            putBoolean(KEY_TRANSFER_DATA, b).apply()
        }
    }

    fun toShowTransferDataDialog(): Boolean {
        return sharedPreferences.getBoolean(KEY_TRANSFER_DATA, false)
    }

}