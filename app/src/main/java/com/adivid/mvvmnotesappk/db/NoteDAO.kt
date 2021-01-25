package com.adivid.mvvmnotesappk.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDAO {

    @Query("Select * from note where isDeleted = 0 AND userId = :uId ORDER BY id DESC")
    fun getAllNotes(uId: String): LiveData<List<Note>>

    @Query("Select * from note WHERE isDataSent = 0 AND userId != '0'")
    fun getNotesToSync(): LiveData<List<Note>>

    @Query("Select * from note WHERE isDeleted = 1")
    fun getNotesToDelete(): LiveData<List<Note>>

    @Query("Select * from note WHERE isDataSent = 0 AND isUpdated = 0")
    suspend fun getInsertedNotesForWorker(): List<Note>

    @Query("Select * from note WHERE isUpdated = 1")
    suspend fun getUpdatedNotesForWorker(): List<Note>

    @Query("Select * from note WHERE isDeleted = 1")
    suspend fun getNotesForDeleting(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: Note): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMultipleNotes(notes: List<Note>): Int

    @Query("SELECT * from note WHERE body LIKE '%'||:string ||'%' AND isDeleted = 0 AND userId = :uId ORDER BY id DESC")
    suspend fun searchNotes(string: String, uId: String): List<Note>

    @Delete
    suspend fun deleteNote(note: Note): Int

    @Delete
    suspend fun deleteMultipleNotes(notes: List<Note>):Int

    @Query("Select * from note WHERE documentId =:docId")
    suspend fun checkIfNoteExists(docId: String?): List<Note>

}
