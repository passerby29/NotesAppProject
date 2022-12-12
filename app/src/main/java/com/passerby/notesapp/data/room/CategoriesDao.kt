package com.passerby.notesapp.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.passerby.notesapp.data.model.CategoryRVModel

@Dao
interface CategoriesDao {

    @Query("select * from Categories")
    fun getAllCategories(): List<CategoryRVModel>

    @Insert
    fun addCategory(item: CategoryRVModel)

    @Delete
    fun remove(item: CategoryRVModel)
}