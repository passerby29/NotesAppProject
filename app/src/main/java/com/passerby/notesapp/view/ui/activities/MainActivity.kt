package com.passerby.notesapp.view.ui.activities

import android.content.*
import android.os.Bundle
import android.text.*
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.*
import com.passerby.notesapp.databinding.ActivityMainBinding
import com.passerby.notesapp.view.adapters.*
import com.passerby.notesapp.view.ui.viewmodels.MainViewModel

class MainActivity : AppCompatActivity(), NotesRVAdapter.NoteClickListener,
    CategoriesChipRVAdapter.CategoryClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notesRVAdapter: NotesRVAdapter
    private lateinit var categoriesRVAdapter: CategoriesChipRVAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View
    private lateinit var preferences: SharedPreferences
    private var searchText: String = ""
    private var category: String = "All notes"
    private lateinit var menu: Menu
    private var sortId = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menu = binding.bottomAppBar.menu
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        preferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)
        val flag = preferences.contains("sortId")

        if (!flag) {
            val editor = preferences.edit()
            editor?.putInt("sortId", 1)
            editor?.apply()
        } else {
            sortId = preferences.getInt("sortId", 1)
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            startActivity(intent)
        }

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
                R.id.sort_by_btn -> {
                    binding.mainSortingContainer.visibility =
                        if (binding.mainSortingContainer.visibility == View.GONE) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    true
                }
                R.id.delete_btn -> {
                    if (item.isEmpty()) {
                        menu.getItem(2)
                            .apply {
                                isEnabled = true
                                icon = ContextCompat.getDrawable(
                                    this@MainActivity,
                                    R.drawable.ic_close
                                )
                            }
                        notesRVAdapter.deleteClick(true)
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
                R.id.placeholder -> {
                    menu.getItem(2)
                        .apply {
                            isEnabled = false
                            icon = ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.stick
                            )
                        }
                    item.clear()
                    notesRVAdapter.deleteClick(!notesRVAdapter.isSelected)
                    binding.mainNotesRv.adapter = notesRVAdapter
                    true
                }
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

    override fun onResume() {
        super.onResume()

        menu.getItem(2)
            .apply {
                isEnabled = false
                icon = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.stick
                )
            }
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        //Notes main recyclerView code
        notesRVAdapter = NotesRVAdapter(this, this)
        viewModel.getAllNotes(sortId).observe(this@MainActivity) {
            it?.let { notesRVAdapter.updateList(it) }
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
                searchText = StringBuilder().append("%").append(p0?.trim()).append("%").toString()
                if (searchText.isNotEmpty()) {
                    if (category == "All notes") {
                        viewModel.getFilterNotes(searchText, sortId).observe(this@MainActivity) {
                            it?.let { notesRVAdapter.updateList(it) }
                        }
                    } else {
                        viewModel.getFilterQueryNotes(searchText, category, sortId)
                            .observe(this@MainActivity) { list ->
                                list?.let { notesRVAdapter.updateList(it) }
                            }
                    }
                } else {
                    if (category == "All notes") {
                        viewModel.getAllNotes(sortId)
                            .observe(this@MainActivity) {
                                it?.let { notesRVAdapter.updateList(it) }
                            }
                    } else {
                        viewModel.getQueryNotes(category, sortId)
                            .observe(this@MainActivity) { list ->
                                list?.let { notesRVAdapter.updateList(it) }
                            }
                    }
                }
                binding.mainNotesRv.adapter = notesRVAdapter
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.apply {
            mainSortByDateBtn1.setOnClickListener { sortList(1) }
            mainSortByDateBtn2.setOnClickListener { sortList(2) }
            mainSortByNameBtn1.setOnClickListener { sortList(3) }
            mainSortByNameBtn2.setOnClickListener { sortList(4) }
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

    private fun sortList(sortId: Int) {
        val editor = preferences.edit()
        editor?.putInt("sortId", sortId)
        editor.apply()
        if (category == "All notes") {
            if (searchText.isEmpty()) {
                viewModel.getAllNotes(sortId).observe(this) {
                    it?.let { notesRVAdapter.updateList(it) }
                }
            } else {
                viewModel.getFilterNotes(searchText, sortId).observe(this) {
                    it?.let { notesRVAdapter.updateList(it) }
                }
            }
        } else {
            if (searchText.isEmpty()) {
                viewModel.getQueryNotes(category, sortId).observe(this) { list ->
                    list?.let { notesRVAdapter.updateList(it) }
                }
            } else {
                viewModel.getFilterQueryNotes(searchText, category, sortId).observe(this) { list ->
                    list?.let { notesRVAdapter.updateList(it) }
                }
            }
        }
        this.sortId = preferences.getInt("sortId", 1)
        binding.mainNotesRv.adapter = notesRVAdapter
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
                viewModel.getAllNotes(sortId).observe(this) {
                    it?.let { notesRVAdapter.updateList(it) }
                }
            } else {
                viewModel.getFilterNotes(searchText, sortId).observe(this) {
                    it?.let { notesRVAdapter.updateList(it) }
                }
            }
        } else {
            if (searchText.isEmpty()) {
                viewModel.getQueryNotes(category, sortId).observe(this) { list ->
                    list?.let { notesRVAdapter.updateList(it) }
                }
            } else {
                viewModel.getFilterQueryNotes(searchText, category, sortId).observe(this) { list ->
                    list?.let { notesRVAdapter.updateList(it) }
                }
            }
        }
        this.category = category
        binding.mainNotesRv.adapter = notesRVAdapter
    }
}