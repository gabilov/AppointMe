package com.beu.pointmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beu.pointmate.data.UserSession
import com.google.android.material.textfield.TextInputEditText

/**
 * Login screen for existing users.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var editEmail: TextInputEditText
    private lateinit var editPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
    }

    private fun setupClickListeners() {
        // Login button
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            performLogin()
        }

        // Forgot password link
        findViewById<TextView>(R.id.textForgotPassword).setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Sign up link
        findViewById<TextView>(R.id.textSignUp).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString()

        // Basic validation
        if (email.isEmpty()) {
            editEmail.error = "Email is required"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.error = "Invalid email format"
            return
        }

        if (password.isEmpty()) {
            editPassword.error = "Password is required"
            return
        }

        // Simulate login (no real authentication)
        UserSession.login(email)

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

