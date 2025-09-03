package com.hackspectra.jobit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class userInputData : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etJobTitle: EditText
    private lateinit var etProjects: EditText
    private lateinit var etExperiences: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnSelectPhoto: Button
    private lateinit var btnSelectResume: Button
    private lateinit var imageView: ImageView

    private var selectedPhotoUri: Uri? = null
    private var selectedResumeUri: Uri? = null

    // Activity results
    private val photoPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedPhotoUri = uri
            imageView.setImageURI(uri)
        }

    private val resumePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedResumeUri = uri
            Toast.makeText(this, "Resume selected", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_input_data)

        auth = FirebaseAuth.getInstance()

        // Init views
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etJobTitle = findViewById(R.id.etJobTitle)
        etProjects = findViewById(R.id.etProjects)
        etExperiences = findViewById(R.id.etExperiences)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnSelectPhoto = findViewById(R.id.btnUploadPhoto)
        btnSelectResume = findViewById(R.id.btnUploadResume)
        imageView = findViewById(R.id.imgProfile)

        btnSelectPhoto.setOnClickListener {
            photoPicker.launch("image/*")
        }

        btnSelectResume.setOnClickListener {
            resumePicker.launch("application/pdf")
        }

        btnSubmit.setOnClickListener {
            if (selectedPhotoUri != null && selectedResumeUri != null) {
                uploadFilesAndData()
                Toast.makeText(this, "Successfully uploaded", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please select photo and resume", Toast.LENGTH_SHORT).show()
            }
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email!!
            val safeEmail = email.replace(".", ",").replace("@", "_at_")

            firestore.collection("users").document(safeEmail).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Autofill form with existing data
                        val user = document.toObject(UserProfile::class.java)
                        user?.let {
                            etName.setText(it.name)
                            etEmail.setText(it.email)
                            etPhone.setText(it.phone)
                            etJobTitle.setText(it.jobTitle)
                            etProjects.setText(it.projects)
                            etExperiences.setText(it.experiences)

                            // Disable editing email if you want to prevent changes
                            etEmail.isEnabled = false

                            // Load profile image
                            Glide.with(this)
                                .load(it.photoUrl)
                                .into(imageView)
                        }

                        Toast.makeText(this, "Profile already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        // No existing profile – proceed as normal
                        Toast.makeText(this, "No profile found. Please fill in the details.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error checking existing data: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun uploadFilesAndData() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val jobTitle = etJobTitle.text.toString().trim()
        val projects = etProjects.text.toString().trim()
        val experiences = etExperiences.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show()
            return
        }

        val safeEmail = email.replace(".", ",").replace("@", "_at_")

        // Default URLs (null if not uploaded)
        var photoUrl: String? = null
        var resumeUrl: String? = null

        fun saveProfile() {
            val profile = UserProfile(
                name, email, phone, jobTitle,
                projects, experiences,
                photoUrl, resumeUrl
            )

            firestore.collection("users").document(safeEmail).set(profile)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile uploaded successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Home_Activity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Firestore error: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        // Case 1: Both files selected → upload both
        if (selectedPhotoUri != null && selectedResumeUri != null) {
            val photoRef = storage.child("photos/$safeEmail.jpg")
            val resumeRef = storage.child("resumes/$safeEmail.pdf")

            photoRef.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    photoRef.downloadUrl.addOnSuccessListener { url ->
                        photoUrl = url.toString()

                        resumeRef.putFile(selectedResumeUri!!)
                            .addOnSuccessListener {
                                resumeRef.downloadUrl.addOnSuccessListener { rUrl ->
                                    resumeUrl = rUrl.toString()
                                    saveProfile()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Resume upload failed, saving without resume.", Toast.LENGTH_SHORT).show()
                                saveProfile()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Photo upload failed, saving without photo.", Toast.LENGTH_SHORT).show()
                    saveProfile()
                }
        }
        // Case 2: Only photo selected
        else if (selectedPhotoUri != null) {
            val photoRef = storage.child("photos/$safeEmail.jpg")
            photoRef.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    photoRef.downloadUrl.addOnSuccessListener { url ->
                        photoUrl = url.toString()
                        saveProfile()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Photo upload failed, saving without photo.", Toast.LENGTH_SHORT).show()
                    saveProfile()
                }
        }
        // Case 3: Only resume selected
        else if (selectedResumeUri != null) {
            val resumeRef = storage.child("resumes/$safeEmail.pdf")
            resumeRef.putFile(selectedResumeUri!!)
                .addOnSuccessListener {
                    resumeRef.downloadUrl.addOnSuccessListener { url ->
                        resumeUrl = url.toString()
                        saveProfile()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Resume upload failed, saving without resume.", Toast.LENGTH_SHORT).show()
                    saveProfile()
                }
        }
        // Case 4: Neither selected → save only Firestore data
        else {
            saveProfile()
        }
    }
}