package com.adivid.mvvmnotesappk.di

import android.content.Context
import androidx.room.Room
import com.adivid.mvvmnotesappk.db.NoteDatabase
import com.adivid.mvvmnotesappk.utils.Constants.NOTE_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNoteDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        NoteDatabase::class.java,
        NOTE_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideNoteDao(
        db: NoteDatabase
    ) = db.getNoteDao()

}