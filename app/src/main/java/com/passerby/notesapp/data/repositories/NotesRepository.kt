package com.passerby.notesapp.data.repositories

import androidx.lifecycle.LiveData
import com.passerby.notesapp.data.room.NotesDao
import com.passerby.notesapp.data.room.NotesEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class NotesRepository(private val notesDao: NotesDao) {

    val notesList: LiveData<List<NotesEntity>> = notesDao.getAllNotes()

    val bookmarkedList: LiveData<List<NotesEntity>> = notesDao.getBookmarks()

    suspend fun newNote(note: NotesEntity) {
        notesDao.newNote(note)
    }

    suspend fun deleteNote(id: Int) {
        notesDao.deleteNote(id)
    }

    suspend fun updateNote(note: NotesEntity) {
        notesDao.updateNote(note)
    }

    fun getCount(): Int = runBlocking {
        val count = async {
            notesDao.getCount()
        }
        count.start()
        count.await()
    }
}