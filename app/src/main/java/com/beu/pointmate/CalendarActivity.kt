package com.beu.pointmate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beu.pointmate.adapter.SchedulableAdapter
import com.beu.pointmate.data.AppointmentRepository
import com.beu.pointmate.model.Appointment
import com.beu.pointmate.model.Schedulable
import java.util.Calendar

/**
 * Calendar activity showing all schedulable items.
 * Demonstrates polymorphism by displaying both Appointments and Tasks.
 */
class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var textEmpty: TextView
    private lateinit var textEventsHeader: TextView

    private lateinit var adapter: SchedulableAdapter
    private var selectedDayStart: Long = 0
    private var selectedDayEnd: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupCalendar()

        // Load events for today
        selectDate(System.currentTimeMillis())
    }

    override fun onResume() {
        super.onResume()
        loadEventsForSelectedDay()
    }

    private fun initViews() {
        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.recyclerEvents)
        textEmpty = findViewById(R.id.textEmpty)
        textEventsHeader = findViewById(R.id.textEventsHeader)
    }

    private fun setupRecyclerView() {
        adapter = SchedulableAdapter(emptyList()) { schedulable ->
            onSchedulableClick(schedulable)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupCalendar() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            selectDate(calendar.timeInMillis)
        }
    }

    private fun selectDate(dateMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis

        // Set to start of day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        selectedDayStart = calendar.timeInMillis

        // Set to end of day
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        selectedDayEnd = calendar.timeInMillis

        loadEventsForSelectedDay()
    }

    private fun loadEventsForSelectedDay() {
        // Get all schedulables (demonstrates polymorphism - both Appointments and Tasks)
        val allSchedulables = AppointmentRepository.getAllSchedulables()

        // Filter by selected day using the Schedulable interface method
        val eventsOnDay = allSchedulables.filter { schedulable ->
            schedulable.occursOn(selectedDayStart, selectedDayEnd)
        }.sortedBy { it.dateTimeMillis }

        adapter.updateData(eventsOnDay)

        if (eventsOnDay.isEmpty()) {
            textEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun onSchedulableClick(schedulable: Schedulable) {
        // Polymorphic behavior - handle different types
        when (schedulable) {
            is Appointment -> {
                val intent = Intent(this, AppointmentDetailActivity::class.java)
                intent.putExtra("appointment_id", schedulable.id)
                startActivity(intent)
            }
            else -> {
                // For Task or other Schedulable types
                Toast.makeText(this, "Task: ${schedulable.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

