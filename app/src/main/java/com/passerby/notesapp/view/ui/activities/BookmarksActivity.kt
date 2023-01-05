package com.passerby.notesapp.view.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.NotesEntity
import com.passerby.notesapp.databinding.ActivityBookmarksBinding
import com.passerby.notesapp.view.adapters.NotesRVAdapter
import com.passerby.notesapp.view.ui.viewmodels.MainViewModel
import java.util.*

@Suppress("DEPRECATION")
class BookmarksActivity : AppCompatActivity(), NotesRVAdapter.NoteClickListener {

    private lateinit var binding: ActivityBookmarksBinding
    private lateinit var notesRVAdapter: NotesRVAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)
        val lang = preferences.getString("lang", Locale.getDefault().toString())

        val myLocale = Locale(lang!!)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)
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

        binding.bookmarksSearchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.getFilterNotes(
                    StringBuilder().append("%").append(p0).append("%").toString(), 2
                ).observe(this@BookmarksActivity) { list ->
                    list?.let { notesRVAdapter.updateList(it) }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun startSearch() {
        binding.bookmarksSearchEt.visibility = View.VISIBLE
        binding.bookmarksSearchBtn.setImageResource(R.drawable.ic_close2)
    }

    private fun closeSearch() {
        binding.bookmarksSearchEt.visibility = View.GONE
        binding.bookmarksSearchBtn.setImageResource(R.drawable.ic_search2)
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