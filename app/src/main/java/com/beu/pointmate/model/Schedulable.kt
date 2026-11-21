package com.beu.pointmate.model

/**
 * ABSTRACTION: Interface representing any schedulable item.
 *
 * This interface demonstrates ABSTRACTION by:
 * - Hiding implementation details from the user
 * - Defining a contract that all schedulable items must follow
 * - Allowing different types (Appointment, Task) to be treated uniformly
 *
 * Used by: Appointment.kt, Task.kt
 */
interface Schedulable {
    // Abstract properties - must be implemented by subclasses
    val id: Int
    val title: String
    val dateTimeMillis: Long

    /**
     * Checks if this schedulable item occurs within the given time range.
     * Default implementation provided - can be overridden by subclasses.
     *
     * @param dayStartMillis Start of the day in milliseconds
     * @param dayEndMillis End of the day in milliseconds
     * @return true if the item's dateTimeMillis falls within the range
     */
    fun occursOn(dayStartMillis: Long, dayEndMillis: Long): Boolean {
        return dateTimeMillis in dayStartMillis..dayEndMillis
    }

    /**
     * Abstract method to get display text - must be implemented by each class.
     * Demonstrates POLYMORPHISM - different classes provide different implementations.
     */
    fun getDisplayText(): String
}

