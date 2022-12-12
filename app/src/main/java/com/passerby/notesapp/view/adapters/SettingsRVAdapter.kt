package com.passerby.notesapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.passerby.notesapp.R
import com.passerby.notesapp.data.model.CategoryRVModel

class SettingsRVAdapter(private val categoriesList: ArrayList<CategoryRVModel>) :
    RecyclerView.Adapter<SettingsRVAdapter.SettingsViewHolder>() {
    lateinit var clickListener: CategoryItemClickListener

    interface CategoryItemClickListener {
        fun deleteCategory(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.settings_rv_item, parent, false)

        return SettingsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.categoryName.text = categoriesList[position].name
        holder.categoryBtn.setOnClickListener { clickListener.deleteCategory(position) }
    }

    override fun getItemCount() = categoriesList.size

    class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.category_name_tv)
        val categoryBtn: AppCompatImageButton = itemView.findViewById(R.id.category_trash_btn)
    }
}