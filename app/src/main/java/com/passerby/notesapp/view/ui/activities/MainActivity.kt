package com.passerby.notesapp.view.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.NotesEntity
import com.passerby.notesapp.databinding.ActivityMainBinding
import com.passerby.notesapp.view.adapters.NotesRVAdapter
import com.passerby.notesapp.view.ui.viewmodels.NotesViewModel

class MainActivity : AppCompatActivity(), NotesRVAdapter.NoteClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notesRVAdapter: NotesRVAdapter
    private lateinit var viewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notesRVAdapter = NotesRVAdapter(this, this)

        binding.mainNotesRv.adapter = notesRVAdapter

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NotesViewModel::class.java]

        viewModel.notesList.observe(this) { list ->
            list?.let { notesRVAdapter.updateList(it) }
        }

        binding.mainNotesTv.text =
            StringBuilder().append(viewModel.notesCount).append(" notes").toString()

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            startActivity(intent)
        }

        binding.mainSearchBtn.setOnClickListener {
            if (binding.mainSearchEt.visibility == View.GONE) {
                startSearch()
            } else {
                closeSearch()
            }
        }

        binding.bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings_nav_btn -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.bookmarks_nav_btn -> {
                    val intent = Intent(this, BookmarksActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun startSearch() {
        binding.mainSearchEt.visibility = View.VISIBLE
        binding.mainSearchBtn.setImageResource(R.drawable.ic_close)
    }

    private fun closeSearch() {
        binding.mainSearchEt.visibility = View.GONE
        binding.mainSearchBtn.setImageResource(R.drawable.ic_search)
    }

    override fun onNoteClick(notes: NotesEntity) {
        val intent = Intent(this@MainActivity, EditNoteActivity::class.java)
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