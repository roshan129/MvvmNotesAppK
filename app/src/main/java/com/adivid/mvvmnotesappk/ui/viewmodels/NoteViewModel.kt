package com.adivid.mvvmnotesappk.ui.viewmodels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.repositories.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Named

class NoteViewModel @ViewModelInject constructor(
    private val mainRepository: NoteRepository,
    auth: FirebaseAuth
) : ViewModel() {

    var firebaseUid = auth.currentUser?.uid ?: "0"

    val allNotes = mainRepository.getAllNotes(firebaseUid)
    val syncNotes = mainRepository.getNotesToSync()
    val deleteNotesFromServer = mainRepository.getNotesToDelete()
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
        val notes = mainRepository.searchNotes(string, firebaseUid)
        searchNotes.value = notes
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        val l = mainRepository.deleteNote(note)
        Timber.d("delete int: $l")
    }

    fun deleteMultipleNotes(notes: List<Note>) = viewModelScope.launch {
        val i = mainRepository.deleteMultipleNotes(notes)
        if (i > 0) {
            repeat(notes.size) {
                val note = notes[it]
                note.isDeleted = 1
                mainRepository.updateNote(note)
            }
        }
    }

    fun updateMultipleNotes(notes: List<Note>) = viewModelScope.launch {
        //val i = mainRepository.updateMultipleNote(notes)
        val list = mutableListOf<Note>()
        repeat(notes.size) {
            val note = notes[it]
            note.isDeleted = 1
            list.add(note)
        }
        val i = mainRepository.updateMultipleNote(list)
        Timber.d("updateMultipleNote: $i")
    }

    fun fetchDataFromFirebase() {

    }


}