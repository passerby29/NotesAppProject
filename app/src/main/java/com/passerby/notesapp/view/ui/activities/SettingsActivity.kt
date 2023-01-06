package com.passerby.notesapp.view.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.passerby.notesapp.R
import com.passerby.notesapp.data.room.CategoriesEntity
import com.passerby.notesapp.databinding.ActivitySettingsBinding
import com.passerby.notesapp.view.adapters.SettingsRVAdapter
import com.passerby.notesapp.view.ui.viewmodels.SettingsViewModel
import java.util.*

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity(), SettingsRVAdapter.CategoryDeleteClickListener {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View
    private lateinit var categoriesRVAdapter: SettingsRVAdapter
    private lateinit var preferences: SharedPreferences
    private lateinit var languages: Array<String?>
    private lateinit var language: String
    private lateinit var themes: Array<String?>
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        preferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)

        val lang = preferences.getString("lang", Locale.getDefault().toString())

        val myLocale = Locale(lang!!)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingsBackBtn.setOnClickListener {
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.settingsAddCategoryBtn.setOnClickListener {
            customAlertDialogView = LayoutInflater.from(this)
                .inflate(R.layout.layout_custom_dialog, binding.root, false)
            launchCustomAlertDialog()
        }

        binding.settingsLangBtn.setOnClickListener {
            languages = arrayOf(
                getString(R.string.language_item_english), getString(R.string.language_item_russian)
            )
            var index = preferences.getInt("langId", 0)

            MaterialAlertDialogBuilder(this).setTitle(getString(R.string.select_language_title))
                .setSingleChoiceItems(languages, index) { _, which ->
                    index = which
                    language = if (index == 0) {
                        "en"
                    } else {
                        "ru"
                    }
                }.setPositiveButton("OK") { _, _ ->
                    val editor = preferences.edit()
                    editor.putString("lang", language)
                    editor.putInt("langId", index)
                    editor.apply()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }.setNegativeButton(getString(R.string.select_language_cancel_button)) { _, _ -> }
                .show()
        }

        binding.settingsThemeBtn.setOnClickListener {
            themes = arrayOf(
                getString(R.string.theme_item_system),
                getString(R.string.theme_item_light),
                getString(R.string.theme_item_dark)
            )
            index = preferences.getInt("themeId", 0)
            MaterialAlertDialogBuilder(this).setTitle(getString(R.string.select_theme_title))
                .setSingleChoiceItems(themes, index) { _, which ->
                    index = which
                }.setPositiveButton("OK") { _, _ ->
                    val editor = preferences.edit()
                    when (index) {
                        0 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        }
                        1 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                        2 -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                    }
                    editor.putInt("themeId", index)
                    editor.apply()
                }.setNegativeButton(getString(R.string.select_theme_cancel_button)) { _, _ -> }
                .show()
        }

        binding.settingsFbBtn.setOnClickListener {
            customAlertDialogView =
                LayoutInflater.from(this)
                    .inflate(R.layout.feedback_layout_dialog, binding.root, false)
            launchCustomAlertDialogFeedback()
        }

        categoriesRVAdapter = SettingsRVAdapter(this, this)

        binding.settingsCategoriesRv.apply {
            layoutManager =
                LinearLayoutManager(this@SettingsActivity, LinearLayoutManager.VERTICAL, false)
            adapter = categoriesRVAdapter
        }

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[SettingsViewModel::class.java]

        viewModel.categoriesList.observe(this) { list ->
            list?.let { categoriesRVAdapter.updateList(it) }
        }
    }

    override fun recreate() {
        super.recreate()
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onDeleteClickListener(item: CategoriesEntity) {
        viewModel.checkCategoryUsing(categoriesRVAdapter.category).observe(this) {
            if (it > 0) {
                MaterialAlertDialogBuilder(
                    this,
                    R.style.DialogAlert
                ).setTitle(getString(R.string.alert_category_title))
                    .setIcon(R.drawable.ic_alert_rectangle)
                    .setMessage(getString(R.string.alert_category_body))
                    .setPositiveButton("OK") { _, _ -> }.show()
            } else {
                viewModel.removeCategory(item)
            }
        }
    }

    private fun launchCustomAlertDialog() {
        val addressTextField: TextInputLayout =
            customAlertDialogView.findViewById(R.id.add_category_name_til)

        // Building the Alert dialog using materialAlertDialogBuilder instance
        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle(getString(R.string.new_category_title))
            .setMessage(getString(R.string.new_category_body))
            .setPositiveButton(getString(R.string.new_category_add_button)) { dialog, _ ->
                val address = addressTextField.editText?.text.toString()
                if (address.isNotEmpty()) {
                    viewModel.addCategory(CategoriesEntity(address))
                    onResume()
                    dialog.dismiss()
                } else {
                    dialog.dismiss()
                }
            }.setNegativeButton(getString(R.string.new_category_cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun launchCustomAlertDialogFeedback() {
        val emailEditText: EditText = customAlertDialogView.findViewById(R.id.dialog_email_et)
        val messageEditText: EditText = customAlertDialogView.findViewById(R.id.dialog_message_et)

        // Building the Alert dialog using materialAlertDialogBuilder instance
        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle(getString(R.string.feedback_title))
            .setIcon(R.drawable.ic_feedback)
            .setPositiveButton(getString(R.string.feedback_send_button)) { dialog, _ ->
                val email = emailEditText.text.toString().trim()
                val message = messageEditText.text.toString().trim()
                if (email.isNotEmpty() && message.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_EMAIL, "xxpasserby@gmail.com")
                    intent.putExtra(Intent.EXTRA_SUBJECT, email)
                    intent.putExtra(Intent.EXTRA_TEXT, message)
                    intent.type = "message/rfc822"
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"))
                    dialog.dismiss()
                } else {
                    dialog.dismiss()
                }
            }.setNegativeButton(getString(R.string.feedback_cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}