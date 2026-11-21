package com.beu.pointmate.data

import android.content.Context
import com.beu.pointmate.model.Appointment
import com.beu.pointmate.model.AppointmentStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * Singleton object responsible for file-based persistence of appointments.
 * Uses JSON format for storage, demonstrating file I/O operations.
 */
object AppointmentStorage {
    private const val FILENAME = "appointments.json"
    private val gson = Gson()

    /**
     * Loads all appointments from the JSON file.
     * @param context Application context for file access
     * @return List of appointments, empty if file doesn't exist or is invalid
     */
    fun load(context: Context): List<Appointment> {
        return try {
            val file = File(context.filesDir, FILENAME)
            if (!file.exists()) {
                emptyList()
            } else {
                val json = file.readText()
                val type = object : TypeToken<List<AppointmentData>>() {}.type
                val dataList: List<AppointmentData> = gson.fromJson(json, type) ?: emptyList()
                dataList.map { it.toAppointment() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Saves all appointments to the JSON file.
     * @param context Application context for file access
     * @param appointments List of appointments to save
     */
    fun save(context: Context, appointments: List<Appointment>) {
        try {
            val file = File(context.filesDir, FILENAME)
            val dataList = appointments.map { AppointmentData.fromAppointment(it) }
            val json = gson.toJson(dataList)
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Data class for JSON serialization (handles enum serialization properly).
     */
    private data class AppointmentData(
        val id: Int,
        val title: String,
        val dateTimeMillis: Long,
        val location: String?,
        val notes: String?,
        val addToCalendar: Boolean,
        val remindersEnabled: Boolean,
        val reminderMinutesBefore: Int?,
        val status: String
    ) {
        fun toAppointment(): Appointment {
            return Appointment(
                id = id,
                title = title,
                dateTimeMillis = dateTimeMillis,
                location = location,
                notes = notes,
                addToCalendar = addToCalendar,
                remindersEnabled = remindersEnabled,
                reminderMinutesBefore = reminderMinutesBefore,
                status = try {
                    AppointmentStatus.valueOf(status)
                } catch (e: Exception) {
                    AppointmentStatus.UPCOMING
                }
            )
        }

        companion object {
            fun fromAppointment(appointment: Appointment): AppointmentData {
                return AppointmentData(
                    id = appointment.id,
                    title = appointment.title,
                    dateTimeMillis = appointment.dateTimeMillis,
                    location = appointment.location,
                    notes = appointment.notes,
                    addToCalendar = appointment.addToCalendar,
                    remindersEnabled = appointment.remindersEnabled,
                    reminderMinutesBefore = appointment.reminderMinutesBefore,
                    status = appointment.status.name
                )
            }
        }
    }
}

