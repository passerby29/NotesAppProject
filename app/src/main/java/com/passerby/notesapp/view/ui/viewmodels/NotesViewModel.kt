package com.passerby.notesapp.view.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.passerby.notesapp.data.repositories.NotesRepository
import com.passerby.notesapp.data.room.NotesAppDB
import com.passerby.notesapp.data.room.NotesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    val notesList: LiveData<List<NotesEntity>>
    val notesCount: Int
    private val repository: NotesRepository

    init {
        val dao = NotesAppDB.getDatabase(application).getNotesDao()
        repository = NotesRepository(dao)
        notesList = repository.notesList
        notesCount = repository.notesCount
    }

    fun deleteNote(note: NotesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
    }

    fun updateNote(note: NotesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNote(note)
    }

    fun newNote(note: NotesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.newNote(note)
    }
}