package com.passerby.notesapp.view.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.passerby.notesapp.data.repositories.CategoriesRepository
import com.passerby.notesapp.data.room.CategoriesEntity
import com.passerby.notesapp.data.room.NotesAppDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    val categoriesList: LiveData<List<CategoriesEntity>>
    private val repository: CategoriesRepository

    init {
        val dao = NotesAppDB.getDatabase(application).getCategoriesDao()
        repository = CategoriesRepository(dao)
        categoriesList = repository.categoriesList
    }

    fun addCategory(category: CategoriesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.addCategory(category)
    }

    fun removeCategory(item: CategoriesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.removeCategory(item)
    }
}