package com.passerby.notesapp.data.repositories

import androidx.lifecycle.LiveData
import com.passerby.notesapp.data.room.NotesDao
import com.passerby.notesapp.data.room.NotesEntity

class NotesRepository(private val notesDao: NotesDao) {

    var notesList: LiveData<List<NotesEntity>> = notesDao.getAllNotes()

    val bookmarkedList: LiveData<List<NotesEntity>> = notesDao.getBookmarks()

    val count: LiveData<Int> = notesDao.getCount()

    suspend fun newNote(note: NotesEntity) {
        notesDao.newNote(note)
    }

    suspend fun deleteNote(id: Int) {
        notesDao.deleteNote(id)
    }

    suspend fun updateNote(note: NotesEntity) {
        notesDao.updateNote(note)
    }

    fun getQueryNotes(category: String): LiveData<List<NotesEntity>> {
        return notesDao.getQueryNotes(category)
    }
}