package com.beu.pointmate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.beu.pointmate.model.Appointment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper object to schedule and cancel appointment reminders.
 */
object ReminderScheduler {

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    /**
     * Schedules a reminder notification for the given appointment.
     */
    fun scheduleReminder(context: Context, appointment: Appointment) {
        // Only schedule if reminders are enabled and reminder time is set
        if (!appointment.remindersEnabled || appointment.reminderMinutesBefore == null) {
            return
        }

        val reminderMinutes = appointment.reminderMinutesBefore
        if (reminderMinutes <= 0) {
            return
        }

        // Calculate reminder time
        val reminderTimeMillis = appointment.dateTimeMillis - (reminderMinutes * 60 * 1000)

        // Don't schedule if reminder time is in the past
        if (reminderTimeMillis <= System.currentTimeMillis()) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_APPOINTMENT_ID, appointment.id)
            putExtra(ReminderReceiver.EXTRA_APPOINTMENT_TITLE, appointment.title)
            putExtra(ReminderReceiver.EXTRA_APPOINTMENT_TIME, timeFormat.format(Date(appointment.dateTimeMillis)))
            putExtra(ReminderReceiver.EXTRA_APPOINTMENT_LOCATION, appointment.location ?: "")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appointment.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ requires checking for exact alarm permission
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                    )
                } else {
                    // Fall back to inexact alarm
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // If exact alarms not permitted, use inexact
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTimeMillis,
                pendingIntent
            )
        }
    }

    /**
     * Cancels a previously scheduled reminder.
     */
    fun cancelReminder(context: Context, appointmentId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appointmentId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}

