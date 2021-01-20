package com.adivid.mvvmnotesappk.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.adivid.mvvmnotesappk.db.NoteDatabase
import com.adivid.mvvmnotesappk.utils.Constants.KEY_EMAIL
import com.adivid.mvvmnotesappk.utils.Constants.NOTE_DATABASE_NAME
import com.adivid.mvvmnotesappk.utils.Constants.SHARED_PREF_NAME
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext app: Context) = app

    @Singleton
    @Provides
    fun provideNoteDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        NoteDatabase::class.java,
        NOTE_DATABASE_NAME
    ).fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideNoteDao(
        db: NoteDatabase
    ) = db.getNoteDao()

    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideSavedEmail(sharedPreferences: SharedPreferences)  =
        sharedPreferences.getString(KEY_EMAIL, "")



}