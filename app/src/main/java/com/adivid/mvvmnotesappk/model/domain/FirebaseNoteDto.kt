package com.adivid.mvvmnotesappk.model.domain

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseNoteDto(
    var id: Int = 0,
    var body: String = "",
    var documentId: String? = null,
    var updated_date: Long? = null,
    var uId: String? = null
) {


}