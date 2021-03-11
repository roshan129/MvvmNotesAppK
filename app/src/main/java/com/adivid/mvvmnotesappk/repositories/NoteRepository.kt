package com.adivid.mvvmnotesappk.repositories

import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.db.NoteDAO
import javax.inject.Inject


class NoteRepository @Inject constructor(
    private val noteDAO: NoteDAO
) {

    suspend fun insertNote(note: Note): Long = noteDAO.insertNote(note)

    suspend fun updateNote(note: Note): Int = noteDAO.updateNote(note)

    suspend fun updateMultipleNote(notes: List<Note>): Int = noteDAO.updateMultipleNotes(notes)

    suspend fun searchNotes(string: String, uId : String): List<Note> = noteDAO.searchNotes(string, uId)

    suspend fun deleteNote(note: Note): Int = noteDAO.deleteNote(note)

    suspend fun deleteMultipleNotes(notes: List<Note>):Int = noteDAO.deleteMultipleNotes(notes)

    fun getAllNotes(uId: String)  = noteDAO.getAllNotes(uId)

    fun getNotesToSync() = noteDAO.getNotesToSync()

    suspend fun getOfflineNotesToSync(): List<Note> = noteDAO.getOfflineNotesToSync()

    fun getNotesToDelete() = noteDAO.getNotesToDelete()

    suspend fun getNotesForWorker() = noteDAO.getInsertedNotesForWorker()


}