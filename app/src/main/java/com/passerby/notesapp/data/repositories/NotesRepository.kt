package com.passerby.notesapp.data.repositories

import androidx.lifecycle.LiveData
import com.passerby.notesapp.data.room.NotesDao
import com.passerby.notesapp.data.room.NotesEntity

class NotesRepository(private val notesDao: NotesDao) {

    val notesList: LiveData<List<NotesEntity>> = notesDao.getAllNotes()

    val notesCount = notesDao.getRowCount()

    suspend fun newNote(note: NotesEntity) {
        notesDao.newNote(note)
    }

    suspend fun deleteNote(note: NotesEntity) {
        notesDao.deleteNote(note)
    }

    suspend fun updateNote(note: NotesEntity) {
        notesDao.updateNote(note)
    }
}