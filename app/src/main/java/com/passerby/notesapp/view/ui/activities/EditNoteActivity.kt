package com.passerby.notesapp.view.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luckyhan.studio.mokaeditor.MokaSpanTool
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.NotesEntity
import com.passerby.notesapp.databinding.ActivityEditNoteBinding
import com.passerby.notesapp.view.ui.viewmodels.EditNoteViewModel
import kotlinx.android.synthetic.main.activity_edit_note.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var viewModel: EditNoteViewModel
    private var noteId = -1
    private var index = 0
    private lateinit var category: String
    private lateinit var categories: Array<String?>
    private lateinit var spanTool: MokaSpanTool
    private lateinit var preferences: SharedPreferences

    @SuppressLint("SimpleDateFormat")
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

        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spanTool = MokaSpanTool(binding.newNoteContentEt)
        binding.newNoteContentEt.textChangeListener = spanTool

        binding.newNoteUndoBtn.setOnClickListener { spanTool.undo() }
        binding.newNoteRedoBtn.setOnClickListener { spanTool.redo() }

        binding.newNoteBackBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //NotesRepository(NotesAppDB.getDatabase(application).getNotesDao()).notesList

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[EditNoteViewModel::class.java]

        viewModel.categoriesList.observe(this) { list ->
            list?.let {
                if (it.isNotEmpty()) {
                    categories = arrayOfNulls(list.size + 1)
                    for (i in 1..list.size) {
                        categories[0] = getString(R.string.category_placeholder)
                        categories[i] = it[i - 1].name
                    }
                } else {
                    categories = arrayOfNulls(0)
                }
            }
        }

        val noteType = intent.getStringExtra("noteType")
        if (noteType.equals("Edit")) {
            val noteTitle = intent.getStringExtra("noteTitle")
            val noteContent = intent.getStringExtra("noteContent")
            val noteDate = intent.getStringExtra("noteDate")
            val noteCategory = intent.getStringExtra("noteCategory")
            var noteBookmark = intent.getBooleanExtra("noteBookmark", false)
            noteId = intent.getIntExtra("noteId", -1)
            binding.newNoteNameEt.setText(noteTitle.toString())
            binding.newNoteContentEt.setText(noteContent.toString())
            binding.newNoteDateTv.text = noteDate.toString()
            category = if (noteCategory == "null") {
                getString(R.string.category_placeholder)
            } else {
                noteCategory.toString()
            }
            binding.newNoteCategoryBtn.text = category
            binding.newNoteBookmarkBtn.apply {
                isChecked = noteBookmark
                setOnClickListener {
                    noteBookmark = when ((it as MaterialButton).isChecked) {
                        true -> {
                            true
                        }
                        false -> {
                            false
                        }
                    }
                }
            }
        } else {
            val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
            val currentDateAndTime: String = sdf.format(System.currentTimeMillis())
            binding.newNoteDateTv.text = currentDateAndTime
            category = getString(R.string.category_placeholder)
        }

        binding.newNoteCategoryBtn.setOnClickListener {
            if (categories.isEmpty()) {
                Toast.makeText(this, "You don't have any category!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.select_category_title))
                    .setSingleChoiceItems(categories, index) { _, which ->
                        index = which
                        category = categories[which].toString()
                    }
                    .setPositiveButton("OK") { _, _ ->
                        binding.newNoteCategoryBtn.text = category
                    }
                    .setNegativeButton(getString(R.string.select_category_cancel_button)) { _, _ -> }
                    .show()
            }
        }

        new_note_conf_btn.setOnClickListener {
            val noteTitle = binding.newNoteNameEt.text.trim().toString()
            val noteContent = binding.newNoteContentEt.text?.trim().toString()
            var noteCategory = binding.newNoteCategoryBtn.text.trim().toString()
            if (noteCategory == getString(R.string.category_placeholder)) {
                noteCategory = "null"
            } else {
                binding.newNoteCategoryBtn.text.trim().toString()
            }
            val noteBookmark = when (binding.newNoteBookmarkBtn.isChecked) {
                true -> {
                    true
                }
                false -> {
                    false
                }
            }

            if (noteType.equals("Edit")) {
                if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                    val updatedNote = NotesEntity(
                        noteTitle,
                        noteContent,
                        System.currentTimeMillis(),
                        noteCategory,
                        noteBookmark
                    )
                    updatedNote.id = noteId
                    viewModel.updateNote(updatedNote)
                }
            } else {
                if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(System.currentTimeMillis())
                    binding.newNoteDateTv.text = currentDateAndTime
                    viewModel.newNote(
                        NotesEntity(
                            noteTitle,
                            noteContent,
                            System.currentTimeMillis(),
                            noteCategory,
                            noteBookmark
                        )
                    )
                }
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}