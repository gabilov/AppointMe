package com.beu.pointmate.model

/**
 * INHERITANCE & POLYMORPHISM: Represents a task item.
 *
 * OOP Concepts demonstrated:
 * - INHERITANCE: Implements Schedulable interface (: Schedulable)
 * - POLYMORPHISM: Overrides getDisplayText() with task-specific implementation
 * - ENCAPSULATION: Data hidden in properties with controlled access
 */
data class Task(
    override val id: Int,                    // INHERITANCE: Overrides from Schedulable
    override val title: String,              // INHERITANCE: Overrides from Schedulable
    override val dateTimeMillis: Long,       // INHERITANCE: Overrides from Schedulable
    val description: String? = null,         // ENCAPSULATION: Task-specific data
    val isCompleted: Boolean = false
) : Schedulable {

    /**
     * POLYMORPHISM: Different implementation than Appointment.getDisplayText()
     * Returns task-specific display text with completion status.
     */
    override fun getDisplayText(): String {
        val statusIcon = if (isCompleted) "✅" else "📋"
        return "$statusIcon $title"
    }

    /**
     * Returns a copy of this task marked as completed.
     * ENCAPSULATION: Provides controlled way to modify state.
     */
    fun markCompleted(): Task {
        return copy(isCompleted = true)
    }
}

