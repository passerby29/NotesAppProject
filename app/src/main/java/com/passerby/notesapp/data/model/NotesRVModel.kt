package com.passerby.notesapp.data.model

import androidx.room.PrimaryKey

data class NotesRVModel(
    var name: String,
    var content: String,
    var date: String,
    var category: String,
    var bookmark: Boolean = true,
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}