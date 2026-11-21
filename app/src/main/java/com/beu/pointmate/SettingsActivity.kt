package com.beu.pointmate

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beu.pointmate.data.SettingsManager

/**
 * Settings activity for configuring app preferences.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var switchUpcoming: SwitchCompat
    private lateinit var switchDailySummary: SwitchCompat
    private lateinit var spinnerDefaultReminder: Spinner

    private val reminderOptions = listOf(
        "None" to 0,
        "10 minutes" to 10,
        "30 minutes" to 30,
        "1 hour" to 60,
        "1 day" to 1440
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ensure SettingsManager is initialized
        SettingsManager.init(this)

        initViews()
        setupReminderSpinner()
        loadSettings()
        setupClickListeners()
    }

    private fun initViews() {
        switchUpcoming = findViewById(R.id.switchUpcoming)
        switchDailySummary = findViewById(R.id.switchDailySummary)
        spinnerDefaultReminder = findViewById(R.id.spinnerDefaultReminder)
    }

    private fun setupReminderSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            reminderOptions.map { it.first }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDefaultReminder.adapter = adapter
    }

    private fun loadSettings() {
        switchUpcoming.isChecked = SettingsManager.getUpcomingNotifications()
        switchDailySummary.isChecked = SettingsManager.getDailySummary()

        val currentReminder = SettingsManager.getDefaultReminder()
        val index = reminderOptions.indexOfFirst { it.second == currentReminder }
        if (index >= 0) {
            spinnerDefaultReminder.setSelection(index)
        }
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveSettings()
        }
    }

    private fun saveSettings() {
        SettingsManager.setUpcomingNotifications(switchUpcoming.isChecked)
        SettingsManager.setDailySummary(switchDailySummary.isChecked)

        val selectedReminder = reminderOptions[spinnerDefaultReminder.selectedItemPosition].second
        SettingsManager.setDefaultReminder(selectedReminder)

        Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()
        finish()
    }
}

