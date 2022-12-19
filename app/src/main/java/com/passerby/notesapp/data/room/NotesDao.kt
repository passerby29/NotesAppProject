package com.passerby.notesapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDao {

    @Query("select * from Notes order by id DESC")
    fun getAllNotes(): LiveData<List<NotesEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newNote(item: NotesEntity)

    @Query("delete from Notes where id = :id")
    suspend fun deleteNote(id: Int)

    @Update
    suspend fun updateNote(item: NotesEntity)

    @Query("SELECT COUNT(*) FROM Notes")
    suspend fun getCount(): Int

    @Query("select * from Notes where bookmark = 1")
    fun getBookmarks(): LiveData<List<NotesEntity>>
}