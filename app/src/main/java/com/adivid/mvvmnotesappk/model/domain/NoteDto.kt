package com.adivid.mvvmnotesappk.model.domain

import java.io.Serializable

data class NoteDto(
    val id: Int?,
    val body: String
): Serializable {
}