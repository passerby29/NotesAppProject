package com.passerby.notesapp.view.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.CategoriesEntity
import com.passerby.notesapp.data.room.NotesEntity
import com.passerby.notesapp.databinding.ActivityMainBinding
import com.passerby.notesapp.view.adapters.CategoriesChipRVAdapter
import com.passerby.notesapp.view.adapters.NotesRVAdapter
import com.passerby.notesapp.view.ui.viewmodels.MainViewModel

class MainActivity : AppCompatActivity(), NotesRVAdapter.NoteClickListener,
    CategoriesChipRVAdapter.CategoryClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notesRVAdapter: NotesRVAdapter
    private lateinit var categoriesRVAdapter: CategoriesChipRVAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View
    private var searchText: String = ""
    private var category: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            startActivity(intent)
        }

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)

        binding.mainAddCategoryBtn.setOnClickListener {
            customAlertDialogView =
                LayoutInflater.from(this).inflate(R.layout.layout_custom_dialog, null, false)
            launchCustomAlertDialog()
        }

        binding.mainSearchBtn.setOnClickListener {
            if (binding.mainSearchEt.visibility == View.GONE) {
                startSearch()
            } else {
                closeSearch()
            }
        }

        binding.bottomAppBar.setOnMenuItemClickListener {
            val item = notesRVAdapter.itemSelectedList
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
                R.id.delete_btn -> {
                    if (item.isEmpty()) {
                        notesRVAdapter.deleteClick(!notesRVAdapter.isSelected)
                        binding.mainNotesRv.adapter = notesRVAdapter
                    } else {
                        MaterialAlertDialogBuilder(this, R.style.DialogAlert)
                            .setIcon(R.drawable.ic_trash)
                            .setTitle("Delete")
                            .setMessage("Do you really want to delete items?")
                            .setPositiveButton("Delete") { _, _ ->
                                for (i in 0 until item.size) {
                                    viewModel.deleteNote(item[i])
                                }
                                item.clear()
                                notesRVAdapter.deleteClick(!notesRVAdapter.isSelected)
                            }
                            .setNegativeButton("Cancel") { _, _ ->
                            }
                            .show()
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        //Notes main recyclerView code
        notesRVAdapter = NotesRVAdapter(this, this)
        viewModel.notesList.observe(this) { list ->
            list?.let { notesRVAdapter.updateList(it) }
        }

        binding.mainNotesRv.adapter = notesRVAdapter
        //Notes main recyclerView code

        //Categories add recyclerView code
        categoriesRVAdapter = CategoriesChipRVAdapter(this, this)
        viewModel.categoriesList.observe(this) { list ->
            list?.let { categoriesRVAdapter.updateList(it) }
        }
        binding.mainCategoriesRv.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriesRVAdapter
        }
        //Categories add recyclerView code

        viewModel.count.observe(this) {
            binding.mainNotesTv.text =
                StringBuilder().append(it.toString()).append(" notes").toString()
        }

        binding.mainSearchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchText = StringBuilder().append("%").append(p0).append("%").toString()
                Toast.makeText(this@MainActivity, searchText, Toast.LENGTH_SHORT).show()
                if (searchText.isNotEmpty()) {
                    Toast.makeText(this@MainActivity, searchText, Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@MainActivity, category, Toast.LENGTH_SHORT).show()
                    if (category == "All notes") {
                        Toast.makeText(this@MainActivity, "getFilterNotes", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.getFilterNotes(searchText).observe(this@MainActivity) {
                            it?.let { notesRVAdapter.updateList(it) }
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity, "getFilterQueryNotes",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.getFilterQueryNotes(searchText, category)
                            .observe(this@MainActivity) { list ->
                                list?.let { notesRVAdapter.updateList(it) }
                            }
                    }
                } else {
                    if (category == "All notes") {
                        Toast.makeText(this@MainActivity, "notesList", Toast.LENGTH_SHORT).show()
                        viewModel.notesList.observe(this@MainActivity) {
                            it?.let { notesRVAdapter.updateList(it) }
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "getQueryNotes", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.getQueryNotes(category)
                            .observe(this@MainActivity) { list ->
                                list?.let { notesRVAdapter.updateList(it) }
                            }
                    }
                }
                binding.mainNotesRv.adapter = notesRVAdapter
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }


    private fun startSearch() {
        binding.mainSearchEt.visibility = View.VISIBLE
        binding.mainSearchBtn.setImageResource(R.drawable.ic_close)
    }

    private fun closeSearch() {
        binding.mainSearchEt.visibility = View.GONE
        binding.mainSearchBtn.setImageResource(R.drawable.ic_search)
    }

    private fun launchCustomAlertDialog() {
        val addressTextField: TextInputLayout =
            customAlertDialogView.findViewById(R.id.add_category_name_til)

        // Building the Alert dialog using materialAlertDialogBuilder instance
        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle("New category")
            .setMessage("Add new category name")
            .setPositiveButton("Add") { dialog, _ ->
                val address = addressTextField.editText?.text.toString()
                if (address.isNotEmpty()) {
                    viewModel.addCategory(CategoriesEntity(address))
                    onResume()
                    dialog.dismiss()
                } else {
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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

    override fun categoryClickListener(category: String) {
        if (category == "All notes") {
            if (searchText.isEmpty()) {
                Toast.makeText(this@MainActivity, "notesList", Toast.LENGTH_SHORT).show()
                viewModel.notesList.observe(this) {
                    it?.let { notesRVAdapter.updateList(it) }
                }
            } else {
                Toast.makeText(this@MainActivity, "getFilterNotes", Toast.LENGTH_SHORT).show()
                viewModel.getFilterNotes(searchText).observe(this) {
                    it?.let { notesRVAdapter.updateList(it) }
                }
            }
        } else {
            if (searchText.isEmpty()) {
                Toast.makeText(this@MainActivity, "getQueryNotes", Toast.LENGTH_SHORT).show()
                viewModel.getQueryNotes(category).observe(this) { list ->
                    list?.let { notesRVAdapter.updateList(it) }
                }
            } else {
                Toast.makeText(this@MainActivity, "getFilterQueryNotes", Toast.LENGTH_SHORT).show()
                viewModel.getFilterQueryNotes(searchText, category).observe(this) { list ->
                    list?.let { notesRVAdapter.updateList(it) }
                }
            }
        }
        this.category = category
        binding.mainNotesRv.adapter = notesRVAdapter
    }
}