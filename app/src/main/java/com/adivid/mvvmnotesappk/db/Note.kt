package com.adivid.mvvmnotesappk.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat

@Entity(tableName = "note")
data class Note(
    var body: String,
    val created: Long = System.currentTimeMillis()

) {
    @PrimaryKey
    var id: Int?= null
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}
