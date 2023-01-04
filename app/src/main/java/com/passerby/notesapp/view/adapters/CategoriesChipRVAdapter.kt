package com.passerby.notesapp.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.CategoriesEntity

@SuppressLint("NotifyDataSetChanged")
class CategoriesChipRVAdapter(
    val context: Context,
    private val categoryClickListener: CategoryClickListener
) : RecyclerView.Adapter<CategoriesChipRVAdapter.ViewHolder>() {

    private var selectedItemPosition: Int = 0
    val categoriesList = ArrayList<CategoriesEntity>()
    var category: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.categories_chips_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        category = categoriesList[position].name
        holder.chipForeground.text = category
        holder.chipForeground.setOnClickListener {
            category = categoriesList[position].name
            selectedItemPosition = holder.adapterPosition
            //showQueryNotesList(category)
            categoryClickListener.categoryClickListener(category)
            notifyDataSetChanged()
        }
        if (selectedItemPosition == holder.adapterPosition)
            holder.chipBackground.setBackgroundResource(R.color.statusBar)
        else
            holder.chipBackground.setBackgroundResource(R.color.chip_background2)
    }

    override fun getItemCount() = categoriesList.size

    fun updateList(newList: List<CategoriesEntity>, category: String) {
        categoriesList.clear()
        categoriesList.add(0, CategoriesEntity(category))
        categoriesList.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chipForeground: MaterialButton = itemView.findViewById(R.id.chip_foreground)
        val chipBackground: LinearLayout = itemView.findViewById(R.id.chip_background)
    }

    interface CategoryClickListener {
        fun categoryClickListener(category: String)
    }
}