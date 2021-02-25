package com.adivid.mvvmnotesappk.mapper

import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.model.domain.FirebaseNoteDto

class FirebaseNoteDtoMapper : EntityMapper<Note, FirebaseNoteDto> {

    override fun mapFromEntity(entity: Note): FirebaseNoteDto {
        val fNote = FirebaseNoteDto(entity.id!!, entity.body)
        fNote.updated_date = entity.updated_date
        fNote.documentId = entity.documentId
        return fNote
    }

    override fun mapToEntity(domainModel: FirebaseNoteDto): Note {
        val note = Note(domainModel.body)
        note.isDataSent = 1
        note.userId = domainModel.uId!!
        note.documentId = domainModel.documentId
        note.updated_date = domainModel.updated_date!!
        return note
    }
}