package com.beu.pointmate.model

/**
 * INHERITANCE & POLYMORPHISM: Represents an appointment with all its details.
 *
 * OOP Concepts demonstrated:
 * - INHERITANCE: Implements Schedulable interface (: Schedulable)
 * - POLYMORPHISM: Overrides getDisplayText() with appointment-specific implementation
 * - ENCAPSULATION: Private method formatDateTime(), data hidden in properties
 * - ABSTRACTION: Implements abstract contract from Schedulable interface
 */
data class Appointment(
    override val id: Int,                    // INHERITANCE: Overrides from Schedulable
    override val title: String,              // INHERITANCE: Overrides from Schedulable
    override val dateTimeMillis: Long,       // INHERITANCE: Overrides from Schedulable
    val location: String? = null,            // ENCAPSULATION: Appointment-specific data
    val notes: String? = null,
    val addToCalendar: Boolean = false,
    val remindersEnabled: Boolean = false,
    val reminderMinutesBefore: Int? = null,
    val status: AppointmentStatus = AppointmentStatus.UPCOMING
) : Schedulable {

    /**
     * POLYMORPHISM: Different implementation than Task.getDisplayText()
     * Returns appointment-specific display text with location.
     */
    override fun getDisplayText(): String {
        val locationText = if (!location.isNullOrBlank()) " at $location" else ""
        return "📅 $title$locationText"
    }

    /**
     * Creates a copy of this appointment with updated status.
     */
    fun withStatus(newStatus: AppointmentStatus): Appointment {
        return copy(status = newStatus)
    }

    /**
     * ENCAPSULATION: Returns a human-readable reminder text.
     * Hides the complexity of converting minutes to readable format.
     */
    fun getReminderText(): String {
        if (!remindersEnabled || reminderMinutesBefore == null) {
            return "No reminder"
        }
        return when (reminderMinutesBefore) {
            10 -> "10 minutes before"
            30 -> "30 minutes before"
            60 -> "1 hour before"
            1440 -> "1 day before"
            else -> "$reminderMinutesBefore minutes before"
        }
    }

    /**
     * Returns a shareable text representation of the appointment.
     */
    fun toShareText(): String {
        val sb = StringBuilder()
        sb.append("📅 $title\n")
        sb.append("📆 ${formatDateTime()}\n")
        if (!location.isNullOrBlank()) {
            sb.append("📍 $location\n")
        }
        if (!notes.isNullOrBlank()) {
            sb.append("📝 $notes\n")
        }
        return sb.toString()
    }

    /**
     * ENCAPSULATION: Private helper method - hidden from outside classes.
     * Only accessible within this class.
     */
    private fun formatDateTime(): String {
        val sdf = java.text.SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(dateTimeMillis))
    }
}

