package com.beu.pointmate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beu.pointmate.data.AppointmentRepository
import com.beu.pointmate.data.SettingsManager
import com.beu.pointmate.data.UserSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Main dashboard activity - the home screen after login.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var textGreeting: TextView
    private lateinit var textDate: TextView
    private lateinit var textNextTitle: TextView
    private lateinit var textNextDateTime: TextView
    private lateinit var textNextLocation: TextView

    private val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    private val appointmentDateFormat = SimpleDateFormat("EEE, MMM d 'at' h:mm a", Locale.getDefault())

    // Permission request launcher for notifications
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result - notifications will work if granted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupClickListeners()
        requestNotificationPermission()

        // Ensure SettingsManager is initialized
        SettingsManager.init(this)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun requestNotificationPermission() {
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun initViews() {
        textGreeting = findViewById(R.id.textGreeting)
        textDate = findViewById(R.id.textDate)
        textNextTitle = findViewById(R.id.textNextTitle)
        textNextDateTime = findViewById(R.id.textNextDateTime)
        textNextLocation = findViewById(R.id.textNextLocation)
    }

    private fun updateUI() {
        // Update greeting
        val name = UserSession.getName().ifEmpty {
            UserSession.getEmail()?.substringBefore("@") ?: "User"
        }
        textGreeting.text = getString(R.string.greeting, name)

        // Update date
        textDate.text = dateFormat.format(Date())

        // Update next appointment
        val upcomingAppointments = AppointmentRepository.getUpcoming()
        if (upcomingAppointments.isNotEmpty()) {
            val next = upcomingAppointments.first()
            textNextTitle.text = next.title
            textNextDateTime.text = appointmentDateFormat.format(Date(next.dateTimeMillis))
            textNextDateTime.visibility = View.VISIBLE

            if (!next.location.isNullOrBlank()) {
                textNextLocation.text = "📍 ${next.location}"
                textNextLocation.visibility = View.VISIBLE
            } else {
                textNextLocation.visibility = View.GONE
            }
        } else {
            textNextTitle.text = getString(R.string.no_upcoming)
            textNextDateTime.visibility = View.GONE
            textNextLocation.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        // New Appointment
        findViewById<Button>(R.id.btnNewAppointment).setOnClickListener {
            val intent = Intent(this, AppointmentEditActivity::class.java)
            startActivity(intent)
        }

        // My Appointments
        findViewById<View>(R.id.btnMyAppointments).setOnClickListener {
            val intent = Intent(this, AppointmentListActivity::class.java)
            startActivity(intent)
        }

        // Calendar
        findViewById<View>(R.id.btnCalendar).setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        // Profile
        findViewById<View>(R.id.btnProfile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Settings
        findViewById<View>(R.id.btnSettings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Profile Avatar (also goes to profile)
        findViewById<View>(R.id.btnProfileAvatar).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Next appointment card click
        findViewById<View>(R.id.cardNextAppointment).setOnClickListener {
            val upcomingAppointments = AppointmentRepository.getUpcoming()
            if (upcomingAppointments.isNotEmpty()) {
                val intent = Intent(this, AppointmentDetailActivity::class.java)
                intent.putExtra("appointment_id", upcomingAppointments.first().id)
                startActivity(intent)
            }
        }
    }
}