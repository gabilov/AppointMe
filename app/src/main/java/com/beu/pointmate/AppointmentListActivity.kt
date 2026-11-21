package com.beu.pointmate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beu.pointmate.adapter.AppointmentAdapter
import com.beu.pointmate.data.AppointmentRepository
import com.beu.pointmate.model.Appointment

/**
 * Activity displaying list of appointments with filtering options.
 */
class AppointmentListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var textEmpty: TextView
    private lateinit var chipAll: TextView
    private lateinit var chipUpcoming: TextView
    private lateinit var chipPast: TextView

    private lateinit var adapter: AppointmentAdapter
    private var currentFilter = FilterType.ALL

    private enum class FilterType { ALL, UPCOMING, PAST }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_appointment_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupFilterChips()
    }

    override fun onResume() {
        super.onResume()
        loadAppointments()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerAppointments)
        textEmpty = findViewById(R.id.textEmpty)
        chipAll = findViewById(R.id.chipAll)
        chipUpcoming = findViewById(R.id.chipUpcoming)
        chipPast = findViewById(R.id.chipPast)
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter(emptyList()) { appointment ->
            openAppointmentDetail(appointment)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFilterChips() {
        chipAll.setOnClickListener { selectFilter(FilterType.ALL) }
        chipUpcoming.setOnClickListener { selectFilter(FilterType.UPCOMING) }
        chipPast.setOnClickListener { selectFilter(FilterType.PAST) }
    }

    private fun selectFilter(filter: FilterType) {
        currentFilter = filter
        updateChipStyles()
        loadAppointments()
    }

    private fun updateChipStyles() {
        // Reset all chips
        listOf(chipAll, chipUpcoming, chipPast).forEach { chip ->
            chip.setBackgroundResource(R.drawable.bg_chip_unselected)
            chip.setTextColor(getColor(R.color.text_secondary))
        }

        // Highlight selected chip
        val selectedChip = when (currentFilter) {
            FilterType.ALL -> chipAll
            FilterType.UPCOMING -> chipUpcoming
            FilterType.PAST -> chipPast
        }
        selectedChip.setBackgroundResource(R.drawable.bg_chip_selected)
        selectedChip.setTextColor(getColor(R.color.primary_blue))
    }

    private fun loadAppointments() {
        val appointments: List<Appointment> = when (currentFilter) {
            FilterType.ALL -> AppointmentRepository.getAll()
            FilterType.UPCOMING -> AppointmentRepository.getUpcoming()
            FilterType.PAST -> AppointmentRepository.getPast()
        }

        adapter.updateData(appointments)

        if (appointments.isEmpty()) {
            textEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun openAppointmentDetail(appointment: Appointment) {
        val intent = Intent(this, AppointmentDetailActivity::class.java)
        intent.putExtra("appointment_id", appointment.id)
        startActivity(intent)
    }
}

