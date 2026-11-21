package com.beu.pointmate.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.beu.pointmate.model.Appointment
import com.beu.pointmate.model.AppointmentStatus

/**
 * SQLite Database Helper for storing appointments.
 * Demonstrates SQLite database operations (CRUD).
 *
 * Database file location: /data/data/com.beu.pointmate/databases/pointmate.db
 */
class AppointmentDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        // Database Info
        const val DATABASE_NAME = "pointmate.db"
        const val DATABASE_VERSION = 1

        // Table Name
        const val TABLE_APPOINTMENTS = "appointments"

        // Column Names
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DATE_TIME = "date_time_millis"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_ADD_TO_CALENDAR = "add_to_calendar"
        const val COLUMN_REMINDERS_ENABLED = "reminders_enabled"
        const val COLUMN_REMINDER_MINUTES = "reminder_minutes_before"
        const val COLUMN_STATUS = "status"
    }

    /**
     * Called when database is created for the first time.
     * Creates the appointments table with all columns.
     */
    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = """
            CREATE TABLE $TABLE_APPOINTMENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DATE_TIME INTEGER NOT NULL,
                $COLUMN_LOCATION TEXT,
                $COLUMN_NOTES TEXT,
                $COLUMN_ADD_TO_CALENDAR INTEGER DEFAULT 0,
                $COLUMN_REMINDERS_ENABLED INTEGER DEFAULT 0,
                $COLUMN_REMINDER_MINUTES INTEGER,
                $COLUMN_STATUS TEXT DEFAULT 'UPCOMING'
            )
        """.trimIndent()

        db.execSQL(createTableSQL)
    }

    /**
     * Called when database needs to be upgraded.
     * Drops old table and creates new one.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENTS")
        onCreate(db)
    }

    /**
     * Inserts a new appointment into the database.
     * @return The row ID of the newly inserted row, or -1 if error
     */
    fun insertAppointment(appointment: Appointment): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, appointment.title)
            put(COLUMN_DATE_TIME, appointment.dateTimeMillis)
            put(COLUMN_LOCATION, appointment.location)
            put(COLUMN_NOTES, appointment.notes)
            put(COLUMN_ADD_TO_CALENDAR, if (appointment.addToCalendar) 1 else 0)
            put(COLUMN_REMINDERS_ENABLED, if (appointment.remindersEnabled) 1 else 0)
            put(COLUMN_REMINDER_MINUTES, appointment.reminderMinutesBefore)
            put(COLUMN_STATUS, appointment.status.name)
        }
        val id = db.insert(TABLE_APPOINTMENTS, null, values)
        db.close()
        return id
    }

    /**
     * Updates an existing appointment in the database.
     * @return Number of rows affected
     */
    fun updateAppointment(appointment: Appointment): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, appointment.title)
            put(COLUMN_DATE_TIME, appointment.dateTimeMillis)
            put(COLUMN_LOCATION, appointment.location)
            put(COLUMN_NOTES, appointment.notes)
            put(COLUMN_ADD_TO_CALENDAR, if (appointment.addToCalendar) 1 else 0)
            put(COLUMN_REMINDERS_ENABLED, if (appointment.remindersEnabled) 1 else 0)
            put(COLUMN_REMINDER_MINUTES, appointment.reminderMinutesBefore)
            put(COLUMN_STATUS, appointment.status.name)
        }
        val rowsAffected = db.update(
            TABLE_APPOINTMENTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(appointment.id.toString())
        )
        db.close()
        return rowsAffected
    }

    /**
     * Deletes an appointment from the database.
     * @return Number of rows deleted
     */
    fun deleteAppointment(id: Int): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(
            TABLE_APPOINTMENTS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return rowsDeleted
    }

    /**
     * Retrieves all appointments from the database.
     * @return List of all appointments
     */
    fun getAllAppointments(): List<Appointment> {
        val appointments = mutableListOf<Appointment>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_APPOINTMENTS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_DATE_TIME ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val appointment = Appointment(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    dateTimeMillis = it.getLong(it.getColumnIndexOrThrow(COLUMN_DATE_TIME)),
                    location = it.getString(it.getColumnIndexOrThrow(COLUMN_LOCATION)),
                    notes = it.getString(it.getColumnIndexOrThrow(COLUMN_NOTES)),
                    addToCalendar = it.getInt(it.getColumnIndexOrThrow(COLUMN_ADD_TO_CALENDAR)) == 1,
                    remindersEnabled = it.getInt(it.getColumnIndexOrThrow(COLUMN_REMINDERS_ENABLED)) == 1,
                    reminderMinutesBefore = it.getInt(it.getColumnIndexOrThrow(COLUMN_REMINDER_MINUTES)).takeIf { min -> min > 0 },
                    status = try {
                        AppointmentStatus.valueOf(it.getString(it.getColumnIndexOrThrow(COLUMN_STATUS)))
                    } catch (e: Exception) {
                        Log.w("AppointmentDB", "Unknown status value", e)
                        AppointmentStatus.UPCOMING
                    }
                )
                appointments.add(appointment)
            }
        }
        db.close()
        return appointments
    }

    /**
     * Retrieves a single appointment by ID.
     * @return Appointment if found, null otherwise
     */
    fun getAppointmentById(id: Int): Appointment? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_APPOINTMENTS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var appointment: Appointment? = null
        cursor.use {
            if (it.moveToFirst()) {
                appointment = Appointment(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    dateTimeMillis = it.getLong(it.getColumnIndexOrThrow(COLUMN_DATE_TIME)),
                    location = it.getString(it.getColumnIndexOrThrow(COLUMN_LOCATION)),
                    notes = it.getString(it.getColumnIndexOrThrow(COLUMN_NOTES)),
                    addToCalendar = it.getInt(it.getColumnIndexOrThrow(COLUMN_ADD_TO_CALENDAR)) == 1,
                    remindersEnabled = it.getInt(it.getColumnIndexOrThrow(COLUMN_REMINDERS_ENABLED)) == 1,
                    reminderMinutesBefore = it.getInt(it.getColumnIndexOrThrow(COLUMN_REMINDER_MINUTES)).takeIf { min -> min > 0 },
                    status = try {
                        AppointmentStatus.valueOf(it.getString(it.getColumnIndexOrThrow(COLUMN_STATUS)))
                    } catch (e: Exception) {
                        Log.w("AppointmentDB", "Unknown status value", e)
                        AppointmentStatus.UPCOMING
                    }
                )
            }
        }
        db.close()
        return appointment
    }

    /**
     * Gets count of all appointments.
     * @return Total number of appointments in database
     */
    fun getAppointmentCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_APPOINTMENTS", null)
        var count = 0
        cursor.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        db.close()
        return count
    }
}
