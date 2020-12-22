package com.adivid.mvvmnotesappk.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    var body: String
) {
    @PrimaryKey
    var id: Int?= null
}
