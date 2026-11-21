package com.beu.pointmate.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton for managing user session state.
 * Simulates authentication without actual backend.
 */
object UserSession {
    private const val PREFS_NAME = "user_session"
    private const val KEY_EMAIL = "email"
    private const val KEY_NAME = "name"
    private const val KEY_PHONE = "phone"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private var prefs: SharedPreferences? = null

    /**
     * Initializes the session manager with context.
     */
    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Simulates user login by storing email.
     */
    fun login(email: String, name: String = "") {
        prefs?.edit()?.apply {
            putString(KEY_EMAIL, email)
            putString(KEY_NAME, name.ifEmpty { email.substringBefore("@") })
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Logs out the current user.
     */
    fun logout() {
        prefs?.edit()?.apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_EMAIL)
            apply()
        }
    }

    /**
     * Checks if a user is currently logged in.
     */
    fun isLoggedIn(): Boolean {
        return prefs?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
    }

    /**
     * Returns the current user's email.
     */
    fun getEmail(): String? {
        return prefs?.getString(KEY_EMAIL, null)
    }

    /**
     * Returns the current user's display name.
     */
    fun getName(): String {
        return prefs?.getString(KEY_NAME, "") ?: ""
    }

    /**
     * Updates the user's name.
     */
    fun setName(name: String) {
        prefs?.edit()?.putString(KEY_NAME, name)?.apply()
    }

    /**
     * Returns the user's phone number.
     */
    fun getPhone(): String {
        return prefs?.getString(KEY_PHONE, "") ?: ""
    }

    /**
     * Updates the user's phone number.
     */
    fun setPhone(phone: String) {
        prefs?.edit()?.putString(KEY_PHONE, phone)?.apply()
    }
}

