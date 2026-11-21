package com.beu.pointmate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.beu.pointmate.data.UserSession
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream

/**
 * Profile activity for viewing and editing user information.
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var textEmail: TextView
    private lateinit var editName: TextInputEditText
    private lateinit var editPhone: TextInputEditText
    private lateinit var imageProfile: ImageView

    private var tempCameraUri: Uri? = null

    // Gallery picker launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            saveProfileImage(it)
            loadProfileImage()
        }
    }

    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            tempCameraUri?.let { uri ->
                saveProfileImage(uri)
                loadProfileImage()
            }
        }
    }

    // Camera permission launcher
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        loadUserData()
        loadProfileImage()
        setupClickListeners()
    }

    private fun initViews() {
        textEmail = findViewById(R.id.textEmail)
        editName = findViewById(R.id.editName)
        editPhone = findViewById(R.id.editPhone)
        imageProfile = findViewById(R.id.imageProfile)
    }

    private fun loadUserData() {
        textEmail.text = UserSession.getEmail() ?: "Not logged in"
        editName.setText(UserSession.getName())
        editPhone.setText(UserSession.getPhone())
    }

    private fun loadProfileImage() {
        val profileImageFile = File(filesDir, "profile_image.jpg")
        if (profileImageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(profileImageFile.absolutePath)
            imageProfile.setImageBitmap(bitmap)
            imageProfile.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun setupClickListeners() {
        // Profile image click
        imageProfile.setOnClickListener {
            showImagePickerDialog()
        }

        // Camera button click
        findViewById<View>(R.id.btnChangePhoto).setOnClickListener {
            showImagePickerDialog()
        }

        // "Tap to change photo" text click
        findViewById<View>(R.id.textChangePhoto).setOnClickListener {
            showImagePickerDialog()
        }

        // Save profile
        findViewById<View>(R.id.btnSaveProfile).setOnClickListener {
            saveProfile()
        }

        // Settings
        findViewById<View>(R.id.btnSettings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Logout
        findViewById<View>(R.id.btnLogout).setOnClickListener {
            performLogout()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("📷 Take Photo", "🖼️ Choose from Gallery", "🗑️ Remove Photo")

        AlertDialog.Builder(this)
            .setTitle("Change Profile Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> openGallery()
                    2 -> removeProfileImage()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val photoFile = File(filesDir, "temp_camera_image.jpg")
        tempCameraUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        tempCameraUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun saveProfileImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Save to internal storage
            val profileImageFile = File(filesDir, "profile_image.jpg")
            val outputStream = FileOutputStream(profileImageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()

            Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeProfileImage() {
        val profileImageFile = File(filesDir, "profile_image.jpg")
        if (profileImageFile.exists()) {
            profileImageFile.delete()
        }
        imageProfile.setImageResource(R.drawable.ic_profile_placeholder)
        Toast.makeText(this, "Profile photo removed", Toast.LENGTH_SHORT).show()
    }

    private fun saveProfile() {
        val name = editName.text.toString().trim()
        val phone = editPhone.text.toString().trim()

        UserSession.setName(name)
        UserSession.setPhone(phone)

        Toast.makeText(this, getString(R.string.profile_saved), Toast.LENGTH_SHORT).show()
    }

    private fun performLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                UserSession.logout()
                val intent = Intent(this, OnboardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}

