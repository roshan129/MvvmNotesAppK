package com.adivid.mvvmnotesappk.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.mapper.FirebaseNoteDtoMapper
import com.adivid.mvvmnotesappk.repositories.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

class NoteViewModel @ViewModelInject constructor(
    private val noteRepository: NoteRepository,
    val auth: FirebaseAuth
) : ViewModel() {

    var firebaseUid = auth.currentUser?.uid ?: "0"

    val allNotes = noteRepository.getAllNotes(firebaseUid)
    val syncNotes = noteRepository.getNotesToSync()
    val deleteNotesFromServer = noteRepository.getNotesToDelete()
    var searchNotes = MutableLiveData<List<Note>>()
    val checkForOfflineData = MutableLiveData<Boolean>()

    fun insertNote(note: Note) = viewModelScope.launch {
        /*return@async mainRepository.insertNote(note)*/
        val l = noteRepository.insertNote(note)
        Timber.d("insert long : $l")
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        val l = noteRepository.updateNote(note)
        Timber.d("update int : $l")
    }

    fun searchNotes(string: String) = viewModelScope.launch {
        val notes = noteRepository.searchNotes(string, firebaseUid)
        searchNotes.value = notes
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        val l = noteRepository.deleteNote(note)
        Timber.d("delete int: $l")
    }

    fun deleteMultipleNotes(notes: List<Note>) = viewModelScope.launch {
        val i = noteRepository.deleteMultipleNotes(notes)
        if (i > 0) {
            repeat(notes.size) {
                val note = notes[it]
                note.isDeleted = 1
                noteRepository.updateNote(note)
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
        val i = noteRepository.updateMultipleNote(list)
        Timber.d("updateMultipleNote: $i")
    }

    fun checkForOfflineData() = viewModelScope.launch {
        val list = noteRepository.getOfflineNotesToSync()
        if (list.isNotEmpty()) checkForOfflineData.postValue(true)
        else checkForOfflineData.postValue(false)
    }

    fun syncOfflineNotes() = viewModelScope.launch {
        val noteList =
            withContext(Dispatchers.Default) { noteRepository.getOfflineNotesToSync() }
        sendOfflineDataToFirebase(noteList)
    }

    private suspend fun sendOfflineDataToFirebase(noteList: List<Note>) {
        Timber.d("inside sendOfflineDataToFirebase")
        val firebaseFirestore = FirebaseFirestore.getInstance()
        if (auth.currentUser != null) {
            repeat(noteList.size) {
                val note = noteList[it]
                val fNote = FirebaseNoteDtoMapper().mapFromEntity(note)
                fNote.uId = firebaseUid
                val documentRef = firebaseFirestore.collection("notes_data").document()
                val docId = documentRef.id
                Timber.d("docId: $docId")
                try {
                    fNote.documentId = docId
                    documentRef.set(fNote).await()
                    Timber.d("Inserted in fire store successfully")
                    note.isDataSent = 1
                    note.documentId = docId
                    note.userId = firebaseUid
                    noteRepository.updateNote(note)
                } catch (e: Exception) {
                    Timber.d("Some error: $e")
                }
            }
        }
    }

}