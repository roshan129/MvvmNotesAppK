package com.adivid.mvvmnotesappk.utils

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }
    })
}

fun ProgressBar.showProgressBar(){
    this.isVisible = true
}

fun ProgressBar.hideProgressBar() {
    this.isVisible = false
}

fun Activity.showToast(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
}