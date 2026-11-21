package com.beu.pointmate.notify

import com.beu.pointmate.model.Appointment

/**
 * Notifier interface to demonstrate runtime polymorphism (one type, many behaviors).
 */
interface Notifier {
    fun notify(appointment: Appointment)
}

