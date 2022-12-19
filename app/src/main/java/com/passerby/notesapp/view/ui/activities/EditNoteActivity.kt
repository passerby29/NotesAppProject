package com.passerby.notesapp.view.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.passerby.notesapp.data.repositories.NotesRepository
import com.passerby.notesapp.data.room.NotesAppDB
import com.passerby.notesapp.data.room.NotesEntity
import com.passerby.notesapp.databinding.ActivityEditNoteBinding
import com.passerby.notesapp.view.ui.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_edit_note.*
import java.text.SimpleDateFormat
import java.util.*

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var viewModel: MainViewModel
    private var noteId = -1

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NotesRepository(NotesAppDB.getDatabase(application).getNotesDao()).notesList

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

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
            binding.newNoteCategoryBtn.text = noteCategory.toString()
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
            val currentDateAndTime: String = sdf.format(Date())
            binding.newNoteDateTv.text = currentDateAndTime
        }

        new_note_conf_btn.setOnClickListener {
            val noteTitle = binding.newNoteNameEt.text.trim().toString()
            val noteContent = binding.newNoteContentEt.text.trim().toString()
            val noteCategory = binding.newNoteCategoryBtn.text.trim().toString()
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
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())
                    val updatedNote =
                        NotesEntity(
                            noteTitle,
                            noteContent,
                            currentDateAndTime,
                            noteCategory,
                            noteBookmark
                        )
                    updatedNote.id = noteId
                    viewModel.updateNote(updatedNote)
                    Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())
                    binding.newNoteDateTv.text = currentDateAndTime
                    viewModel.newNote(
                        NotesEntity(
                            noteTitle,
                            noteContent,
                            currentDateAndTime,
                            noteCategory,
                            noteBookmark
                        )
                    )
                    Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
                }
            }
            onBackPressedDispatcher.onBackPressed()
        }
    }
}