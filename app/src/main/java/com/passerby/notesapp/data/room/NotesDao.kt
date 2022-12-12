package com.passerby.notesapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDao {

    @Query("select * from Notes order by id ASC")
    fun getAllNotes(): LiveData<List<NotesEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newNote(item: NotesEntity)

    @Delete
    suspend fun deleteNote(item: NotesEntity)

    @Update
    suspend fun updateNote(item: NotesEntity)

    @Query("select count(*) from notes")
    fun getRowCount(): Int
}