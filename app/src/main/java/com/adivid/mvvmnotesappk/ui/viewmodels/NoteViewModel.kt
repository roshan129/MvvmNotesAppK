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
        Timber.d("insert long : $l")
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        val l = mainRepository.upateNote(note)
        Timber.d("update int : $l")
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        val l = mainRepository.deleteNote(note)
        Timber.d("delete int: $l")
    }

    fun deleteMultipleNotes(notes: List<Note>) = viewModelScope.launch {
        mainRepository.deleteMultipleNotes(notes)
    }


}