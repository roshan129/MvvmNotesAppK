package com.adivid.mvvmnotesappk.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities =[Note::class],
    version = 7,
    exportSchema = false
)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun getNoteDao() : NoteDAO

}
