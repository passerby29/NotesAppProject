package com.passerby.notesapp.data.repositories

import androidx.lifecycle.LiveData
import com.passerby.notesapp.data.room.NotesDao
import com.passerby.notesapp.data.room.NotesEntity

class NotesRepository(private val notesDao: NotesDao) {

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

    fun getAllNotes(sortId: Int): LiveData<List<NotesEntity>> {
        return notesDao.getAllNotes(sortId)
    }

    fun getQueryNotes(category: String, sortId: Int): LiveData<List<NotesEntity>> {
        return notesDao.getQueryNotes(category, sortId)
    }

    fun getFilterNotes(filter: String, sortId: Int): LiveData<List<NotesEntity>> {
        return notesDao.getFilterNotes(filter, sortId)
    }

    fun getFilterQueryNotes(
        filter: String,
        category: String,
        sortId: Int
    ): LiveData<List<NotesEntity>> {
        return notesDao.getFilterQueryNotes(filter, category, sortId)
    }
}