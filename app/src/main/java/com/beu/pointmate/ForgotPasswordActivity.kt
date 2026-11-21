package com.beu.pointmate

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

/**
 * Forgot password screen - simulates password reset.
 */
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var editEmail: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editEmail = findViewById(R.id.editEmail)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Send reset link button
        findViewById<Button>(R.id.btnSendReset).setOnClickListener {
            sendResetLink()
        }

        // Back to login link
        findViewById<TextView>(R.id.textBackToLogin).setOnClickListener {
            finish()
        }
    }

    private fun sendResetLink() {
        val email = editEmail.text.toString().trim()

        if (email.isEmpty()) {
            editEmail.error = "Email is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.error = "Invalid email format"
            return
        }

        // Simulate sending reset link
        Toast.makeText(this, getString(R.string.reset_link_sent), Toast.LENGTH_LONG).show()
        finish()
    }
}

