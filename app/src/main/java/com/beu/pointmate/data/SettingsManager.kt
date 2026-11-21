package com.beu.pointmate.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton for managing app settings using SharedPreferences.
 */
object SettingsManager {
    private const val PREFS_NAME = "app_settings"
    private const val KEY_UPCOMING_NOTIFICATIONS = "upcoming_notifications"
    private const val KEY_DAILY_SUMMARY = "daily_summary"
    private const val KEY_DEFAULT_REMINDER = "default_reminder"

    @Volatile
    private var prefs: SharedPreferences? = null

    private var appContext: Context? = null

    /**
     * Initializes the settings manager with context.
     */
    fun init(context: Context) {
        if (prefs == null) {
            appContext = context.applicationContext
            prefs = appContext!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun getPrefs(): SharedPreferences? {
        if (prefs == null && appContext != null) {
            prefs = appContext!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
        return prefs
    }

    /**
     * Gets the upcoming notifications setting.
     */
    fun getUpcomingNotifications(): Boolean {
        return getPrefs()?.getBoolean(KEY_UPCOMING_NOTIFICATIONS, true) ?: true
    }

    /**
     * Sets the upcoming notifications setting.
     */
    fun setUpcomingNotifications(enabled: Boolean) {
        getPrefs()?.edit()?.putBoolean(KEY_UPCOMING_NOTIFICATIONS, enabled)?.commit()
    }

    /**
     * Gets the daily summary notification setting.
     */
    fun getDailySummary(): Boolean {
        return getPrefs()?.getBoolean(KEY_DAILY_SUMMARY, false) ?: false
    }

    /**
     * Sets the daily summary notification setting.
     */
    fun setDailySummary(enabled: Boolean) {
        getPrefs()?.edit()?.putBoolean(KEY_DAILY_SUMMARY, enabled)?.commit()
    }

    /**
     * Gets the default reminder time in minutes.
     * Returns 0 for no reminder.
     */
    fun getDefaultReminder(): Int {
        return getPrefs()?.getInt(KEY_DEFAULT_REMINDER, 30) ?: 30
    }

    /**
     * Sets the default reminder time in minutes.
     */
    fun setDefaultReminder(minutes: Int) {
        getPrefs()?.edit()?.putInt(KEY_DEFAULT_REMINDER, minutes)?.commit()
    }
}

