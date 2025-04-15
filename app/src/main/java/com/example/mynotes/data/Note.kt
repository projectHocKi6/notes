package com.example.mynotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val context: String = "",
    val day: String,
    val status: Int,
    val cover: String,
    val coverOn: Boolean,
    val userUID: String = "",
){
    constructor(): this(0, "","","",0,"",false,"",)
}
