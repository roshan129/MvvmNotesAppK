package com.adivid.mvvmnotesappk.utils

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.adivid.mvvmnotesappk.db.NoteDAO
import com.adivid.mvvmnotesappk.mapper.FirebaseNoteDtoMapper
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception

class FirebaseWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val dao: NoteDAO
) : CoroutineWorker(context, params) {

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUid: String

    override suspend fun doWork(): Result {
        auth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseUid = auth.currentUser!!.uid
        if (auth.currentUser != null) {

            saveInsertedDataToFirebase()

            saveUpdatedDataToFirebase()

            deleteNotesFromFirebase()

        } else {
            Timber.d("current user is null")
        }
        return Result.success()
    }

    private suspend fun saveInsertedDataToFirebase() {
        val list = dao.getInsertedNotesForWorker()
        repeat(list.size) {
            val note = list[it]
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
                dao.updateNote(note)
            } catch (e: Exception) {
                Timber.d("Some error: $e")
            }
        }
    }

    private suspend fun saveUpdatedDataToFirebase() {
        val list = dao.getUpdatedNotesForWorker()
        repeat(list.size) { i: Int ->
            val note = list[i]
            val fNote = FirebaseNoteDtoMapper().mapFromEntity(note)
            fNote.uId = firebaseUid
            try {
                val documentRef = firebaseFirestore.collection("notes_data")
                    .document(note.documentId!!)
                documentRef.set(fNote)
                note.isUpdated = 0
                note.isDataSent = 1
                dao.updateNote(note)
                Timber.d("saveUpdatedDataToFirebase: updated")

            } catch (e: Exception) {
                Timber.d("saveUpdatedDataToFirebase: exception: $e")
            }
        }
    }

    private suspend fun deleteNotesFromFirebase() {
        val deleteList = dao.getNotesForDeleting()
        repeat(deleteList.size){
            try {
                val documentRef = firebaseFirestore.collection("notes_data").
                document(deleteList[it].documentId!!)
                documentRef.delete()
                dao.deleteNote(deleteList[it])
                Timber.d("deleteNotesFromFirebase: deleted")
            }catch (e: Exception){
                Timber.d("deleteNotesFromFirebase: Error Occurred: $e")
            }
        }
    }

}