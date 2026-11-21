package com.beu.pointmate.data

import android.content.Context
import com.beu.pointmate.model.Appointment
import com.beu.pointmate.model.AppointmentStatus
import com.beu.pointmate.model.Schedulable
import com.beu.pointmate.model.Task

/**
 * Singleton repository for managing appointments.
 * Demonstrates encapsulation and provides CRUD operations.
 * Uses SQLite database for persistent storage.
 */
object AppointmentRepository {
    private val appointments = mutableListOf<Appointment>()
    private var context: Context? = null
    private var nextId = 1
    private var databaseHelper: AppointmentDatabaseHelper? = null

    /**
     * Initializes the repository by loading data from SQLite database.
     * Should be called at app startup.
     */
    fun init(context: Context) {
        this.context = context.applicationContext

        // Initialize SQLite database helper
        databaseHelper = AppointmentDatabaseHelper(this.context!!)

        // Load appointments from SQLite database
        val loaded = databaseHelper!!.getAllAppointments()
        appointments.clear()
        appointments.addAll(loaded)
        nextId = (appointments.maxOfOrNull { it.id } ?: 0) + 1

        // Seed sample data if empty
        if (appointments.isEmpty()) {
            seedSampleData()
        }
        fun init(context: Context) {
            // ... existing code ...

            // Add this NEW block:
            val now = System.currentTimeMillis()
            appointments.forEach { appointment ->
                if (appointment.status == AppointmentStatus.UPCOMING && appointment.dateTimeMillis < now) {
                    // Auto-complete past appointments
                    val updated = appointment.withStatus(AppointmentStatus.COMPLETED)
                    update(updated)
                }
            }
        }
    }

    /**
     * Returns all appointments.
     */
    fun getAll(): List<Appointment> {
        return appointments.toList()
    }

    /**
     * Returns only upcoming appointments (future appointments with UPCOMING status).
     */
    fun getUpcoming(): List<Appointment> {
        val now = System.currentTimeMillis()
        return appointments.filter {
            it.status == AppointmentStatus.UPCOMING && it.dateTimeMillis >= now
        }.sortedBy { it.dateTimeMillis }
    }

    /**
     * Returns past appointments (either completed, cancelled, or past date).
     */
    fun getPast(): List<Appointment> {
        val now = System.currentTimeMillis()
        return appointments.filter {
            it.status != AppointmentStatus.UPCOMING || it.dateTimeMillis < now
        }.sortedByDescending { it.dateTimeMillis }
    }

    /**
     * Returns an appointment by its ID.
     */
    fun getById(id: Int): Appointment? {
        return appointments.find { it.id == id }
    }

    /**
     * Creates a new appointment.
     * Saves to SQLite database.
     * @return The created appointment with assigned ID
     */
    fun create(
        title: String,
        dateTimeMillis: Long,
        location: String? = null,
        notes: String? = null,
        addToCalendar: Boolean = false,
        remindersEnabled: Boolean = false,
        reminderMinutesBefore: Int? = null
    ): Appointment {
        val appointment = Appointment(
            id = nextId++,
            title = title,
            dateTimeMillis = dateTimeMillis,
            location = location,
            notes = notes,
            addToCalendar = addToCalendar,
            remindersEnabled = remindersEnabled,
            reminderMinutesBefore = reminderMinutesBefore,
            status = AppointmentStatus.UPCOMING
        )
        appointments.add(appointment)

        // Save to SQLite database
        databaseHelper?.insertAppointment(appointment)

        return appointment
    }

    /**
     * Updates an existing appointment.
     * Updates in SQLite database.
     */
    fun update(appointment: Appointment) {
        val index = appointments.indexOfFirst { it.id == appointment.id }
        if (index >= 0) {
            appointments[index] = appointment

            // Update in SQLite database
            databaseHelper?.updateAppointment(appointment)
        }
    }

    /**
     * Deletes an appointment by ID.
     * Removes from SQLite database.
     */
    fun delete(id: Int) {
        appointments.removeAll { it.id == id }

        // Delete from SQLite database
        databaseHelper?.deleteAppointment(id)
    }

    /**
     * Returns all schedulable items (appointments + sample tasks).
     * Demonstrates polymorphism by returning List<Schedulable>.
     */
    fun getAllSchedulables(): List<Schedulable> {
        val schedulables = mutableListOf<Schedulable>()
        schedulables.addAll(appointments)
        // Add sample tasks to demonstrate polymorphism
        schedulables.addAll(getSampleTasks())
        return schedulables
    }

    /**
     * Returns sample tasks to demonstrate polymorphism.
     */
    private fun getSampleTasks(): List<Task> {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L
        return listOf(
            Task(
                id = -1,
                title = "Review study materials",
                dateTimeMillis = now + oneDay,
                description = "Prepare for upcoming exam"
            ),
            Task(
                id = -2,
                title = "Submit project report",
                dateTimeMillis = now + (2 * oneDay),
                description = "Final project deadline"
            )
        )
    }


    /**
     * Seeds sample data for demonstration purposes.
     */
    private fun seedSampleData() {
        val now = System.currentTimeMillis()
        val oneHour = 60 * 60 * 1000L
        val oneDay = 24 * oneHour

        create(
            title = "Doctor Appointment",
            dateTimeMillis = now + (2 * oneDay) + (10 * oneHour),
            location = "City Medical Center",
            notes = "Annual checkup",
            remindersEnabled = true,
            reminderMinutesBefore = 60
        )

        create(
            title = "Team Meeting",
            dateTimeMillis = now + oneDay + (3 * oneHour),
            location = "Conference Room A",
            notes = "Discuss project progress",
            remindersEnabled = true,
            reminderMinutesBefore = 30
        )

        create(
            title = "Dentist Visit",
            dateTimeMillis = now + (5 * oneDay) + (14 * oneHour),
            location = "Smile Dental Clinic",
            remindersEnabled = true,
            reminderMinutesBefore = 1440
        )
    }
}

