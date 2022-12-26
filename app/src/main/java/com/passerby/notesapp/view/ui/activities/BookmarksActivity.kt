package com.passerby.notesapp.view.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.NotesEntity
import com.passerby.notesapp.databinding.ActivityBookmarksBinding
import com.passerby.notesapp.view.adapters.NotesRVAdapter
import com.passerby.notesapp.view.ui.viewmodels.MainViewModel

class BookmarksActivity : AppCompatActivity(), NotesRVAdapter.NoteClickListener {

    private lateinit var binding: ActivityBookmarksBinding
    private lateinit var notesRVAdapter: NotesRVAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bookmarksBackBtn.setOnClickListener {
            val intent = Intent(this@BookmarksActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.bookmarksSearchBtn.setOnClickListener {
            if (binding.bookmarksSearchEt.visibility == View.GONE) {
                startSearch()
            } else {
                closeSearch()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        notesRVAdapter = NotesRVAdapter(this, this)
        viewModel.bookmarkedList.observe(this) { list ->
            list?.let { notesRVAdapter.updateList(it) }
        }

        binding.bookmarksNotesRv.adapter = notesRVAdapter
    }

    private fun startSearch() {
        binding.bookmarksSearchEt.visibility = View.VISIBLE
        binding.bookmarksSearchBtn.setImageResource(R.drawable.ic_close)
    }

    private fun closeSearch() {
        binding.bookmarksSearchEt.visibility = View.GONE
        binding.bookmarksSearchBtn.setImageResource(R.drawable.ic_search)
    }

    override fun onNoteClick(notes: NotesEntity) {
        val intent = Intent(this@BookmarksActivity, EditNoteActivity::class.java)
        intent.putExtra("noteType", "Edit")
        intent.putExtra("noteTitle", notes.name)
        intent.putExtra("noteContent", notes.content)
        intent.putExtra("noteDate", notes.date)
        intent.putExtra("noteCategory", notes.category)
        intent.putExtra("noteBookmark", notes.bookmark)
        intent.putExtra("noteId", notes.id)
        startActivity(intent)
    }
}