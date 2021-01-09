package com.adivid.mvvmnotesappk.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDAO {

    @Query("Select * from note ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: Note) : Int

    @Delete
    suspend fun deleteNote(note: Note): Int

    @Delete
    suspend fun deleteMultipleNotes(notes: List<Note>)

}
