package com.passerby.notesapp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NotesEntity::class],
    exportSchema = false,
    version = 1
)
abstract class NotesAppDB : RoomDatabase() {

    abstract fun getNotesDao(): NotesDao

    //abstract fun getCategoriesDao(): CategoriesDao

    companion object {
        @Volatile
        private var INSTANCE: NotesAppDB? = null
        fun getDatabase(context: Context): NotesAppDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesAppDB::class.java,
                    "notes_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}