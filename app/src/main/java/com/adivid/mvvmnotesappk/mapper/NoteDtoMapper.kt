package com.adivid.mvvmnotesappk.mapper

import com.adivid.mvvmnotesappk.db.Note
import com.adivid.mvvmnotesappk.model.domain.NoteDto

class NoteDtoMapper : EntityMapper<Note, NoteDto>{

    override fun mapFromEntity(entity: Note): NoteDto {
        val noteDto =NoteDto(entity.id, entity.body)
        noteDto.docId = entity.documentId
        return noteDto
    }

    override fun mapToEntity(domainModel: NoteDto): Note {
        val note =  Note(domainModel.body)
        note.id = domainModel.id
        return note
    }

}