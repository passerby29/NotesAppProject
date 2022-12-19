package com.passerby.notesapp.data.repositories

import androidx.lifecycle.LiveData
import com.passerby.notesapp.data.room.CategoriesDao
import com.passerby.notesapp.data.room.CategoriesEntity

class CategoriesRepository(private val categoriesDao: CategoriesDao) {

    val categoriesList: LiveData<List<CategoriesEntity>> = categoriesDao.getAllCategories()

    suspend fun addCategory(category: CategoriesEntity) {
        categoriesDao.addCategory(category)
    }

    suspend fun removeCategory(item: CategoriesEntity) {
        categoriesDao.removeCategory(item)
    }
}