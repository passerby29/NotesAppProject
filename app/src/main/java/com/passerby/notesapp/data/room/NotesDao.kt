package com.passerby.notesapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDao {

    @Query(
        "select * from Notes order by " +
                "case when :sortId = 1 then date end asc, " +
                "case when :sortId = 2 then date end desc, " +
                "case when :sortId = 3 then name end asc, " +
                "case when :sortId = 4 then name end desc"
    )
    fun getAllNotes(sortId: Int): LiveData<List<NotesEntity>>

    @Query("SELECT COUNT(*) FROM Notes")
    fun getCount(): LiveData<Int>

    @Query("select * from Notes where bookmark = 1 order by date DESC")
    fun getBookmarks(): LiveData<List<NotesEntity>>

    @Query(
        "select * from Notes where category = :category order by " +
                "case when :sortId = 1 then date end asc, " +
                "case when :sortId = 2 then date end desc, " +
                "case when :sortId = 3 then name end asc, " +
                "case when :sortId = 4 then name end desc"
    )
    fun getQueryNotes(category: String, sortId: Int): LiveData<List<NotesEntity>>

    @Query("select * from Notes where name like :filter order by " +
            "case when :sortId = 1 then date end asc, " +
            "case when :sortId = 2 then date end desc, " +
            "case when :sortId = 3 then name end asc, " +
            "case when :sortId = 4 then name end desc"
    )
    fun getFilterNotes(filter: String, sortId: Int): LiveData<List<NotesEntity>>

    @Query("select * from Notes where name like :filter and category = :category order by " +
            "case when :sortId = 1 then date end asc, " +
            "case when :sortId = 2 then date end desc, " +
            "case when :sortId = 3 then name end asc, " +
            "case when :sortId = 4 then name end desc"
    )
    fun getFilterQueryNotes(filter: String, category: String, sortId: Int): LiveData<List<NotesEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun newNote(item: NotesEntity)

    @Query("delete from Notes where id = :id")
    suspend fun deleteNote(id: Int)

    @Update
    suspend fun updateNote(item: NotesEntity)
}