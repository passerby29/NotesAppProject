package com.passerby.notesapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoriesDao {

    @Query("select * from Categories order by id ASC")
    fun getAllCategories(): LiveData<List<CategoriesEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCategory(item: CategoriesEntity)

    @Delete
    suspend fun removeCategory(item: CategoriesEntity)
}