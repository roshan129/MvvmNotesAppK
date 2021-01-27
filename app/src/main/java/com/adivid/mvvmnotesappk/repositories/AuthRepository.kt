package com.adivid.mvvmnotesappk.repositories

import android.util.Log
import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.db.NoteDAO
import com.adivid.mvvmnotesappk.mapper.FirebaseNoteDtoMapper
import com.adivid.mvvmnotesappk.model.domain.FirebaseNoteDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val dao: NoteDAO
) {

    suspend fun registerUser(email: String, password: String): Boolean {
        auth.createUserWithEmailAndPassword(email, password).await()
        return auth.currentUser != null
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        auth.signInWithEmailAndPassword(email, password).await()
        return auth.currentUser != null
    }

    suspend fun firebaseAuthWithGoogle(idToken: String): FirebaseUser?{
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val task = auth.signInWithCredential(credential).await()
            val user = task.user
            user
        }catch (e: Exception){
            Timber.d("firebaseAuthWithGoogle exception: $e")
            null
        }
    }

    suspend fun fetchDataFromFirebase() {
        try {
            val documentSnapshot = firebaseFirestore.collection("notes_data").whereEqualTo(
                "uid",
                auth.currentUser!!.uid
            ).get().await()

            if(!documentSnapshot.isEmpty){
                val fNoteDtolist = documentSnapshot.toObjects(FirebaseNoteDto::class.java)
                Timber.d("fetchDataFromFirebase suspend: ${fNoteDtolist[0].body}")
                val noteList = mutableListOf<Note>()
                repeat(fNoteDtolist.size){
                    val note = FirebaseNoteDtoMapper().mapToEntity(fNoteDtolist[it])
                    noteList.add(note)
                }
                insertServerDataInDb(noteList)

            }
        }catch (e: Exception){
            Timber.d("fetchDataFromFirebase exception: $e")
        }
    }

    private suspend fun insertServerDataInDb(noteList: MutableList<Note>) {
        repeat(noteList.size){
            if(!checkIfNoteExists(noteList[it].documentId)){
                dao.insertNote(noteList[it])
            }
        }

    }

    private suspend fun checkIfNoteExists(docId: String?): Boolean {
        docId.let {
            val list = dao.checkIfNoteExists(docId)
            if(list.isNotEmpty()) return true
        }
        return false
    }

}