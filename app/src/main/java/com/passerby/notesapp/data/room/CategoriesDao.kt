package com.passerby.notesapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoriesDao {

    @Query("select * from Categories order by id ASC")
    fun getAllCategories(): LiveData<List<CategoriesEntity>>

    @Query("select count(*) from Notes where category = :category")
    fun checkCategoryUsing(category: String): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCategory(item: CategoriesEntity)

    @Delete
    suspend fun removeCategory(item: CategoriesEntity)
}