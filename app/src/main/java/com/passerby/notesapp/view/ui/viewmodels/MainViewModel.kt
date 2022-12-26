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


class MainViewModel(application: Application) : AndroidViewModel(application) {

    var notesList: LiveData<List<NotesEntity>>
    private val notesRepository: NotesRepository
    private val notesDao = NotesAppDB.getDatabase(application).getNotesDao()
    val count: LiveData<Int>

    //
    val categoriesList: LiveData<List<CategoriesEntity>>
    private val categoriesRepository: CategoriesRepository
    private val categoriesDao = NotesAppDB.getDatabase(application).getCategoriesDao()

    //Bookmarks
    val bookmarkedList: LiveData<List<NotesEntity>>

    init {
        notesRepository = NotesRepository(notesDao)
        notesList = notesRepository.notesList
        count = notesRepository.count
        //
        categoriesRepository = CategoriesRepository(categoriesDao)
        categoriesList = categoriesRepository.categoriesList
        //
        bookmarkedList = notesRepository.bookmarkedList
        //
    }

    fun deleteNote(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        notesRepository.deleteNote(id)
    }

    fun addCategory(category: CategoriesEntity) = viewModelScope.launch(Dispatchers.IO) {
        categoriesRepository.addCategory(category)
    }

    fun getQueryNotes(category: String): LiveData<List<NotesEntity>> {
        return notesRepository.getQueryNotes(category)
    }

    fun getFilterNotes(filter: String): LiveData<List<NotesEntity>> {
        return notesRepository.getFilterNotes(filter)
    }

    fun getFilterQueryNotes(filter: String, category: String): LiveData<List<NotesEntity>>{
        return notesRepository.getFilterQueryNotes(filter, category)
    }
}