package com.beu.pointmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beu.pointmate.data.UserSession
import com.google.android.material.textfield.TextInputEditText

/**
 * Registration screen for new users.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var editName: TextInputEditText
    private lateinit var editEmail: TextInputEditText
    private lateinit var editPassword: TextInputEditText
    private lateinit var editConfirmPassword: TextInputEditText
    private lateinit var checkTerms: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        editName = findViewById(R.id.editName)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        editConfirmPassword = findViewById(R.id.editConfirmPassword)
        checkTerms = findViewById(R.id.checkTerms)
    }

    private fun setupClickListeners() {
        // Sign up button
        findViewById<Button>(R.id.btnSignUp).setOnClickListener {
            performRegistration()
        }

        // Login link
        findViewById<TextView>(R.id.textLogin).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun performRegistration() {
        val name = editName.text.toString().trim()
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString()
        val confirmPassword = editConfirmPassword.text.toString()

        // Validation
        if (name.isEmpty()) {
            editName.error = "Name is required"
            return
        }

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

        if (password.length < 6) {
            editPassword.error = "Password must be at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            editConfirmPassword.error = "Passwords do not match"
            return
        }

        if (!checkTerms.isChecked) {
            Toast.makeText(this, "Please accept the Terms & Privacy Policy", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulate registration (no real backend)
        UserSession.login(email, name)

        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

