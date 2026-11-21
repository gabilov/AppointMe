package com.beu.pointmate

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beu.pointmate.data.AppointmentRepository
import com.beu.pointmate.data.SettingsManager
import com.beu.pointmate.model.Appointment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Activity for creating or editing appointments.
 */
class AppointmentEditActivity : AppCompatActivity() {

    private lateinit var textHeader: TextView
    private lateinit var editTitle: TextInputEditText
    private lateinit var editLocation: TextInputEditText
    private lateinit var editNotes: TextInputEditText
    private lateinit var btnPickDate: TextView
    private lateinit var btnPickTime: TextView
    private lateinit var checkAddToCalendar: CheckBox
    private lateinit var checkReminders: CheckBox
    private lateinit var layoutReminder: LinearLayout
    private lateinit var spinnerReminder: Spinner

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    private var appointmentId: Int? = null
    private var existingAppointment: Appointment? = null

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
        setContentView(R.layout.activity_appointment_edit)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        appointmentId = intent.getIntExtra("appointment_id", -1).takeIf { it != -1 }

        initViews()
        setupReminderSpinner()
        setupClickListeners()
        loadExistingAppointment()
    }

    private fun initViews() {
        textHeader = findViewById(R.id.textHeader)
        editTitle = findViewById(R.id.editTitle)
        editLocation = findViewById(R.id.editLocation)
        editNotes = findViewById(R.id.editNotes)
        btnPickDate = findViewById(R.id.btnPickDate)
        btnPickTime = findViewById(R.id.btnPickTime)
        checkAddToCalendar = findViewById(R.id.checkAddToCalendar)
        checkReminders = findViewById(R.id.checkReminders)
        layoutReminder = findViewById(R.id.layoutReminder)
        spinnerReminder = findViewById(R.id.spinnerReminder)

        // Set default date/time to now + 1 hour
        calendar.add(Calendar.HOUR_OF_DAY, 1)
        calendar.set(Calendar.MINUTE, 0)
        updateDateTimeButtons()
    }

    private fun setupReminderSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            reminderOptions.map { it.first }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerReminder.adapter = adapter

        // Set default reminder from settings
        val defaultReminder = SettingsManager.getDefaultReminder()
        val defaultIndex = reminderOptions.indexOfFirst { it.second == defaultReminder }
        if (defaultIndex >= 0) {
            spinnerReminder.setSelection(defaultIndex)
        }
    }

    private fun setupClickListeners() {
        btnPickDate.setOnClickListener { showDatePicker() }
        btnPickTime.setOnClickListener { showTimePicker() }

        checkReminders.setOnCheckedChangeListener { _, isChecked ->
            layoutReminder.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener { saveAppointment() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
    }

    private fun loadExistingAppointment() {
        appointmentId?.let { id ->
            existingAppointment = AppointmentRepository.getById(id)
            existingAppointment?.let { appointment ->
                textHeader.text = getString(R.string.edit_appointment)
                editTitle.setText(appointment.title)
                editLocation.setText(appointment.location ?: "")
                editNotes.setText(appointment.notes ?: "")

                calendar.timeInMillis = appointment.dateTimeMillis
                updateDateTimeButtons()

                checkAddToCalendar.isChecked = appointment.addToCalendar
                checkReminders.isChecked = appointment.remindersEnabled

                if (appointment.remindersEnabled && appointment.reminderMinutesBefore != null) {
                    layoutReminder.visibility = View.VISIBLE
                    val index = reminderOptions.indexOfFirst { it.second == appointment.reminderMinutesBefore }
                    if (index >= 0) {
                        spinnerReminder.setSelection(index)
                    }
                }
            }
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateTimeButtons()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateDateTimeButtons()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun updateDateTimeButtons() {
        btnPickDate.text = dateFormat.format(calendar.time)
        btnPickTime.text = timeFormat.format(calendar.time)
    }

    private fun saveAppointment() {
        val title = editTitle.text.toString().trim()

        if (title.isEmpty()) {
            editTitle.error = "Title is required"
            return
        }

        val location = editLocation.text.toString().trim().takeIf { it.isNotEmpty() }
        val notes = editNotes.text.toString().trim().takeIf { it.isNotEmpty() }
        val addToCalendar = checkAddToCalendar.isChecked
        val remindersEnabled = checkReminders.isChecked
        val reminderMinutes = if (remindersEnabled) {
            reminderOptions[spinnerReminder.selectedItemPosition].second.takeIf { it > 0 }
        } else null

        val savedAppointment: com.beu.pointmate.model.Appointment

        if (existingAppointment != null) {
            // Cancel old reminder first
            ReminderScheduler.cancelReminder(this, existingAppointment!!.id)

            // Update existing
            val updated = existingAppointment!!.copy(
                title = title,
                dateTimeMillis = calendar.timeInMillis,
                location = location,
                notes = notes,
                addToCalendar = addToCalendar,
                remindersEnabled = remindersEnabled,
                reminderMinutesBefore = reminderMinutes
            )
            AppointmentRepository.update(updated)
            savedAppointment = updated
        } else {
            // Create new
            savedAppointment = AppointmentRepository.create(
                title = title,
                dateTimeMillis = calendar.timeInMillis,
                location = location,
                notes = notes,
                addToCalendar = addToCalendar,
                remindersEnabled = remindersEnabled,
                reminderMinutesBefore = reminderMinutes
            )
        }

        // Schedule reminder notification if enabled
        if (remindersEnabled && reminderMinutes != null && reminderMinutes > 0) {
            ReminderScheduler.scheduleReminder(this, savedAppointment)
        }

        Toast.makeText(this, getString(R.string.appointment_saved), Toast.LENGTH_SHORT).show()
        finish()
    }
}

