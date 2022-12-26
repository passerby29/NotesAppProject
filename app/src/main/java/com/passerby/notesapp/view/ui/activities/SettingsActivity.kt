package com.passerby.notesapp.view.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.CategoriesEntity
import com.passerby.notesapp.databinding.ActivitySettingsBinding
import com.passerby.notesapp.view.adapters.SettingsRVAdapter
import com.passerby.notesapp.view.ui.viewmodels.SettingsViewModel

class SettingsActivity : AppCompatActivity(), SettingsRVAdapter.CategoryDeleteClickListener {

    lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)

        binding.settingsBackBtn.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.settingsAddCategoryBtn.setOnClickListener {
            customAlertDialogView =
                LayoutInflater.from(this).inflate(R.layout.layout_custom_dialog, null, false)
            launchCustomAlertDialog()
        }

        val categoriesRVAdapter = SettingsRVAdapter(this, this)

        binding.settingsCategoriesRv.apply {
            layoutManager =
                LinearLayoutManager(this@SettingsActivity, LinearLayoutManager.VERTICAL, false)
            adapter = categoriesRVAdapter
        }

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[SettingsViewModel::class.java]

        viewModel.categoriesList.observe(this, Observer { list ->
            list?.let { categoriesRVAdapter.updateList(it) }
        })
    }

    override fun onDeleteClickListener(item: CategoriesEntity) {
        viewModel.removeCategory(item)
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
}