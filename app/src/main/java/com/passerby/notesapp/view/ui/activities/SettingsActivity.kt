package com.passerby.notesapp.view.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.passerby.notesapp.data.model.CategoryRVModel
import com.passerby.notesapp.databinding.ActivitySettingsBinding
import com.passerby.notesapp.view.adapters.SettingsRVAdapter

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsRVAdapter: SettingsRVAdapter
    private lateinit var categoriesList: ArrayList<CategoryRVModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoriesList = java.util.ArrayList()
        settingsRVAdapter = SettingsRVAdapter(categoriesList)
        settingsRVAdapter.clickListener = categoryItemClickListener

        for (i in 0 until 11) {
            categoriesList.add(CategoryRVModel("Work notes"))
        }

        binding.settingsBackBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.settingsCategoriesRv.adapter = settingsRVAdapter
    }

    private val categoryItemClickListener = object : SettingsRVAdapter.CategoryItemClickListener {
        override fun deleteCategory(position: Int) {
            categoriesList.removeAt(position)
            onResume()
        }
    }
}