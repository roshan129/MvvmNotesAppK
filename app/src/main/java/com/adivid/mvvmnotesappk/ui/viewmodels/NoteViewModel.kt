package com.adivid.mvvmnotesappk.ui.viewmodels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.repositories.MainRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber


class NoteViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    val allNotes = mainRepository.getAllNotes()

    fun insertNote(note: Note) = viewModelScope.launch {
        /*return@async mainRepository.insertNote(note)*/
        val l = mainRepository.insertNote(note)
        Timber.d("long : $l")
    }




}