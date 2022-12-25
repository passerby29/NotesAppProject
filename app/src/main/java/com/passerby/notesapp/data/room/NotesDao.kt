package com.passerby.notesapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDao {

    @Query("select * from Notes order by id DESC")
    fun getAllNotes(): LiveData<List<NotesEntity>>

    @Query("SELECT COUNT(*) FROM Notes")
    fun getCount(): LiveData<Int>

    @Query("select * from Notes where bookmark = 1")
    fun getBookmarks(): LiveData<List<NotesEntity>>

    @Query("select * from Notes where category = :category")
    fun getQueryNotes(category: String): LiveData<List<NotesEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newNote(item: NotesEntity)

    @Query("delete from Notes where id = :id")
    suspend fun deleteNote(id: Int)

    @Update
    suspend fun updateNote(item: NotesEntity)
}