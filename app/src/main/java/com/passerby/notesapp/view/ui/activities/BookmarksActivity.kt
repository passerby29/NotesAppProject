package com.passerby.notesapp.view.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.passerby.notesapp.data.room.NotesEntity
import com.passerby.notesapp.databinding.ActivityBookmarksBinding
import com.passerby.notesapp.view.adapters.NotesRVAdapter

class BookmarksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookmarksBinding
    private lateinit var notesRVAdapter: NotesRVAdapter
    private lateinit var notesList: ArrayList<NotesEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}