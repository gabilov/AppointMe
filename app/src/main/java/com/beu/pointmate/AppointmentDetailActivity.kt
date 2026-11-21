package com.beu.pointmate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beu.pointmate.data.AppointmentRepository
import com.beu.pointmate.model.Appointment
import com.beu.pointmate.model.AppointmentStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Activity displaying appointment details with actions.
 */
class AppointmentDetailActivity : AppCompatActivity() {

    private lateinit var textStatus: TextView
    private lateinit var textTitle: TextView
    private lateinit var textDate: TextView
    private lateinit var textTime: TextView
    private lateinit var textLocation: TextView
    private lateinit var layoutLocation: LinearLayout
    private lateinit var textNotes: TextView
    private lateinit var layoutNotes: LinearLayout
    private lateinit var textReminder: TextView
    private lateinit var btnMap: View
    private lateinit var btnCancelAppointment: View

    private val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    private var appointmentId: Int = -1
    private var appointment: Appointment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_appointment_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        appointmentId = intent.getIntExtra("appointment_id", -1)

        initViews()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        loadAppointment()
    }

    private fun initViews() {
        textStatus = findViewById(R.id.textStatus)
        textTitle = findViewById(R.id.textTitle)
        textDate = findViewById(R.id.textDate)
        textTime = findViewById(R.id.textTime)
        textLocation = findViewById(R.id.textLocation)
        layoutLocation = findViewById(R.id.layoutLocation)
        textNotes = findViewById(R.id.textNotes)
        layoutNotes = findViewById(R.id.layoutNotes)
        textReminder = findViewById(R.id.textReminder)
        btnMap = findViewById(R.id.btnMap)
        btnCancelAppointment = findViewById(R.id.btnCancelAppointment)
    }

    private fun loadAppointment() {
        appointment = AppointmentRepository.getById(appointmentId)

        if (appointment == null) {
            Toast.makeText(this, "Appointment not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val apt = appointment!!

        // Update status
        when (apt.status) {
            AppointmentStatus.UPCOMING -> {
                textStatus.text = getString(R.string.status_upcoming)
                textStatus.setBackgroundResource(R.drawable.bg_status_upcoming)
                textStatus.setTextColor(getColor(R.color.status_upcoming))
                btnCancelAppointment.visibility = View.VISIBLE
            }
            AppointmentStatus.COMPLETED -> {
                textStatus.text = getString(R.string.status_completed)
                textStatus.setBackgroundResource(R.drawable.bg_status_completed)
                textStatus.setTextColor(getColor(R.color.status_completed))
                btnCancelAppointment.visibility = View.GONE
            }
            AppointmentStatus.CANCELLED -> {
                textStatus.text = getString(R.string.status_cancelled)
                textStatus.setBackgroundResource(R.drawable.bg_status_cancelled)
                textStatus.setTextColor(getColor(R.color.status_cancelled))
                btnCancelAppointment.visibility = View.GONE
            }
        }

        textTitle.text = apt.title
        textDate.text = dateFormat.format(Date(apt.dateTimeMillis))
        textTime.text = timeFormat.format(Date(apt.dateTimeMillis))

        // Location
        if (!apt.location.isNullOrBlank()) {
            textLocation.text = apt.location
            layoutLocation.visibility = View.VISIBLE
            btnMap.visibility = View.VISIBLE
        } else {
            layoutLocation.visibility = View.GONE
            btnMap.visibility = View.GONE
        }

        // Notes
        if (!apt.notes.isNullOrBlank()) {
            textNotes.text = apt.notes
            layoutNotes.visibility = View.VISIBLE
        } else {
            layoutNotes.visibility = View.GONE
        }

        // Reminder
        textReminder.text = apt.getReminderText()
    }

    private fun setupClickListeners() {
        // Edit
        findViewById<View>(R.id.btnEdit).setOnClickListener {
            val intent = Intent(this, AppointmentEditActivity::class.java)
            intent.putExtra("appointment_id", appointmentId)
            startActivity(intent)
        }

        // Share - Implicit Intent
        findViewById<View>(R.id.btnShare).setOnClickListener {
            shareAppointment()
        }

        // Open in Map - Implicit Intent
        btnMap.setOnClickListener {
            openInMap()
        }

        // Cancel Appointment
        btnCancelAppointment.setOnClickListener {
            cancelAppointment()
        }

        // Delete Appointment
        findViewById<View>(R.id.btnDelete).setOnClickListener {
            deleteAppointment()
        }
    }

    private fun shareAppointment() {
        appointment?.let { apt ->
            val shareText = apt.toShareText()
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, apt.title)
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        }
    }

    private fun openInMap() {
        appointment?.location?.let { location ->
            try {
                // Try Google Maps first with search query
                val encodedLocation = Uri.encode(location)
                val googleMapsUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedLocation")
                val mapIntent = Intent(Intent.ACTION_VIEW, googleMapsUri)
                startActivity(mapIntent)
            } catch (e: Exception) {
                // Fallback to geo URI if Google Maps URL doesn't work
                try {
                    val encodedLocation = Uri.encode(location)
                    val geoUri = Uri.parse("geo:0,0?q=$encodedLocation")
                    val geoIntent = Intent(Intent.ACTION_VIEW, geoUri)
                    startActivity(geoIntent)
                } catch (e2: Exception) {
                    Toast.makeText(this, "No map application found", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(this, "No location specified", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelAppointment() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.cancel_appointment))
            .setMessage("Are you sure you want to cancel this appointment?")
            .setPositiveButton("Yes") { _, _ ->
                appointment?.let { apt ->
                    // Cancel any scheduled reminder
                    ReminderScheduler.cancelReminder(this, apt.id)

                    val updated = apt.withStatus(AppointmentStatus.CANCELLED)
                    AppointmentRepository.update(updated)
                    Toast.makeText(this, getString(R.string.appointment_cancelled), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteAppointment() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage("Are you sure you want to permanently delete this appointment?")
            .setPositiveButton("Delete") { _, _ ->
                // Cancel any scheduled reminder
                ReminderScheduler.cancelReminder(this, appointmentId)

                AppointmentRepository.delete(appointmentId)
                Toast.makeText(this, getString(R.string.appointment_deleted), Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

