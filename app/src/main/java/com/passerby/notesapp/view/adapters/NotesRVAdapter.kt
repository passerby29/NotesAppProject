package com.passerby.notesapp.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.NotesEntity
import java.text.SimpleDateFormat

class NotesRVAdapter(
    val context: Context,
    private val noteClickListener: NoteClickListener,
) : RecyclerView.Adapter<NotesRVAdapter.NotesViewHolder>() {

    val notesList = ArrayList<NotesEntity>()
    var isSelected: Boolean = false
    val itemSelectedList = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.notes_rv_item, parent, false)

        return NotesViewHolder(itemView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
        holder.notesNameTV.text = notesList[position].name
        holder.notesContentTV.text = notesList[position].content
        holder.notesDateTV.text = sdf.format(notesList[position].date)
        holder.notesCategoryTV.text = if (notesList[position].category == "null") {
            context.getString(R.string.category_placeholder)
        } else {
            notesList[position].category
        }
        holder.notesBookmarkIV.visibility = if (notesList[position].bookmark) {
            View.VISIBLE
        } else {
            View.GONE
        }
        holder.notesCheckboxIV.visibility = if (isSelected) {
            View.VISIBLE
        } else {
            View.GONE
        }
        holder.itemView.setOnClickListener {
            if (isSelected) {
                if (itemSelectedList.contains(notesList[position].id)) {
                    itemSelectedList.removeAt(position)
                    holder.notesCheckboxIV.setBackgroundResource(R.drawable.ic_checkbox)
                } else {
                    itemSelectedList.add(notesList[position].id)
                    holder.notesCheckboxIV.setBackgroundResource(R.drawable.ic_checkbox_checked)
                }
            } else {
                noteClickListener.onNoteClick(notesList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<NotesEntity>) {
        notesList.clear()
        notesList.addAll(newList)
        notifyDataSetChanged()
    }

    fun deleteClick(isSelected: Boolean) {
        this.isSelected = isSelected
    }

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notesNameTV: TextView = itemView.findViewById(R.id.notes_name_tv)
        val notesContentTV: TextView = itemView.findViewById(R.id.notes_content_tv)
        val notesDateTV: TextView = itemView.findViewById(R.id.notes_date_tv)
        val notesCategoryTV: TextView = itemView.findViewById(R.id.notes_category_tv)
        val notesBookmarkIV: ImageView = itemView.findViewById(R.id.notes_bookmark_iv)
        val notesCheckboxIV: ImageView = itemView.findViewById(R.id.notes_checkbox_iv)
    }

    interface NoteClickListener {
        fun onNoteClick(notes: NotesEntity)
    }
}