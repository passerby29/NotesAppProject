package com.passerby.notesapp.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.CategoriesEntity

class SettingsRVAdapter(
    val context: Context,
    private val categoryDeleteClickListener: CategoryDeleteClickListener,
) : RecyclerView.Adapter<SettingsRVAdapter.SettingsViewHolder>() {

    private val allCategories = ArrayList<CategoriesEntity>()

    interface CategoryItemClickListener {}

    interface CategoryDeleteClickListener {
        fun onDeleteClickListener(item: CategoriesEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.settings_rv_item, parent, false)

        return SettingsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.categoryName.text = allCategories[position].name
        holder.categoryBtn.setOnClickListener {
            categoryDeleteClickListener.onDeleteClickListener(
                allCategories[position]
            )
        }
    }

    fun updateList(newList: List<CategoriesEntity>) {
        allCategories.clear()
        allCategories.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = allCategories.size

    class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.category_name_tv)
        val categoryBtn: AppCompatImageButton = itemView.findViewById(R.id.category_trash_btn)
    }
}