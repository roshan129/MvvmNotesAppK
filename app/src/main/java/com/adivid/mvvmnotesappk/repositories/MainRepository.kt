package com.adivid.mvvmnotesappk.repositories

import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.db.NoteDAO
import javax.inject.Inject


class MainRepository @Inject constructor(
    val noteDAO: NoteDAO
) {

    suspend fun insertNote(note: Note): Long = noteDAO.insertNote(note)

    suspend fun upateNote(note: Note): Int = noteDAO.updateNote(note)

    suspend fun deleteNote(note: Note): Int = noteDAO.deleteNote(note)

    fun getAllNotes()  = noteDAO.getAllNotes()

}