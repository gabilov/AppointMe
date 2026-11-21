package com.beu.pointmate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * BroadcastReceiver that handles appointment reminder notifications.
 */
class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "appointment_reminders"
        const val CHANNEL_NAME = "Appointment Reminders"
        const val EXTRA_APPOINTMENT_ID = "appointment_id"
        const val EXTRA_APPOINTMENT_TITLE = "appointment_title"
        const val EXTRA_APPOINTMENT_TIME = "appointment_time"
        const val EXTRA_APPOINTMENT_LOCATION = "appointment_location"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val appointmentId = intent.getIntExtra(EXTRA_APPOINTMENT_ID, -1)
        val title = intent.getStringExtra(EXTRA_APPOINTMENT_TITLE) ?: "Appointment"
        val time = intent.getStringExtra(EXTRA_APPOINTMENT_TIME) ?: ""
        val location = intent.getStringExtra(EXTRA_APPOINTMENT_LOCATION) ?: ""

        // Create notification channel for Android 8.0+
        createNotificationChannel(context)

        // Create intent to open appointment details when notification is tapped
        val detailIntent = Intent(context, AppointmentDetailActivity::class.java).apply {
            putExtra("appointment_id", appointmentId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            appointmentId,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification content
        val contentText = if (location.isNotEmpty()) {
            "📍 $location\n🕐 $time"
        } else {
            "🕐 $time"
        }

        // Build and show notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⏰ Upcoming: $title")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(appointmentId, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming appointments"
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

