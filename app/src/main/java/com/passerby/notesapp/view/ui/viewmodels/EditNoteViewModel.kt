package com.passerby.notesapp.view.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.passerby.notesapp.data.repositories.CategoriesRepository
import com.passerby.notesapp.data.repositories.NotesRepository
import com.passerby.notesapp.data.room.CategoriesEntity
import com.passerby.notesapp.data.room.NotesAppDB
import com.passerby.notesapp.data.room.NotesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditNoteViewModel(application: Application): AndroidViewModel(application) {

    private val notesRepository: NotesRepository
    private val notesDao = NotesAppDB.getDatabase(application).getNotesDao()

    //
    val categoriesList: LiveData<List<CategoriesEntity>>
    private val categoriesRepository: CategoriesRepository
    private val categoriesDao = NotesAppDB.getDatabase(application).getCategoriesDao()

    init {
        notesRepository = NotesRepository(notesDao)
        //
        categoriesRepository = CategoriesRepository(categoriesDao)
        categoriesList = categoriesRepository.categoriesList
    }
    fun updateNote(note: NotesEntity) = viewModelScope.launch(Dispatchers.IO) {
        notesRepository.updateNote(note)
    }

    fun newNote(note: NotesEntity) = viewModelScope.launch(Dispatchers.IO) {
        notesRepository.newNote(note)
    }
}