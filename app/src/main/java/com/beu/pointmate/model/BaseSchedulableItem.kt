package com.beu.pointmate.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ABSTRACTION: Abstract base class for all schedulable items.
 *
 * This abstract class demonstrates ABSTRACTION by:
 * - Providing common functionality for all schedulable items
 * - Defining abstract methods that subclasses MUST implement
 * - Hiding implementation details from users of the class
 *
 * Cannot be instantiated directly - must be extended by concrete classes.
 */
abstract class BaseSchedulableItem {

    // Abstract properties - MUST be implemented by subclasses
    abstract val id: Int
    abstract val title: String
    abstract val dateTimeMillis: Long

    /**
     * ABSTRACTION: Abstract method - each subclass provides its own implementation.
     * This forces all schedulable items to define how they display themselves.
     */
    abstract fun getItemType(): String

    /**
     * ABSTRACTION: Abstract method for getting item icon.
     */
    abstract fun getIcon(): String

    /**
     * Concrete method - shared implementation for all subclasses.
     * ENCAPSULATION: Hides the complexity of date formatting.
     */
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return sdf.format(Date(dateTimeMillis))
    }

    /**
     * Concrete method - shared implementation for all subclasses.
     */
    fun getFormattedTime(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date(dateTimeMillis))
    }

    /**
     * Concrete method - shared implementation for all subclasses.
     */
    fun getFormattedDateTime(): String {
        return "${getFormattedDate()} at ${getFormattedTime()}"
    }

    /**
     * Checks if this item is in the past.
     */
    fun isPast(): Boolean {
        return dateTimeMillis < System.currentTimeMillis()
    }

    /**
     * Checks if this item is today.
     */
    fun isToday(): Boolean {
        val now = System.currentTimeMillis()
        val startOfDay = now - (now % (24 * 60 * 60 * 1000))
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000)
        return dateTimeMillis in startOfDay until endOfDay
    }
}

