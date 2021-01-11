package com.adivid.mvvmnotesappk.ui.viewmodels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.repositories.MainRepository
import kotlinx.coroutines.launch
import timber.log.Timber


class NoteViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val allNotes = mainRepository.getAllNotes()
    var searchNotes = MutableLiveData<List<Note>>()

    fun insertNote(note: Note) = viewModelScope.launch {
        /*return@async mainRepository.insertNote(note)*/
        val l = mainRepository.insertNote(note)
        Timber.d("insert long : $l")
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        val l = mainRepository.updateNote(note)
        Timber.d("update int : $l")
    }

    fun searchNotes(string: String) = viewModelScope.launch {
        val notes = mainRepository.searchNotes(string)
        searchNotes.value = notes
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        val l = mainRepository.deleteNote(note)
        Timber.d("delete int: $l")
    }

    fun deleteMultipleNotes(notes: List<Note>) = viewModelScope.launch {
        mainRepository.deleteMultipleNotes(notes)
    }


}