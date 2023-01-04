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

    fun getAllNotes(sortId: Int): LiveData<List<NotesEntity>> {
        return notesRepository.getAllNotes(sortId)
    }

    fun getQueryNotes(category: String, sortId: Int): LiveData<List<NotesEntity>> {
        return notesRepository.getQueryNotes(category, sortId)
    }

    fun getFilterNotes(filter: String, sortId: Int): LiveData<List<NotesEntity>> {
        return notesRepository.getFilterNotes(filter, sortId)
    }

    fun getFilterQueryNotes(
        filter: String,
        category: String,
        sortId: Int
    ): LiveData<List<NotesEntity>> {
        return notesRepository.getFilterQueryNotes(filter, category, sortId)
    }


}