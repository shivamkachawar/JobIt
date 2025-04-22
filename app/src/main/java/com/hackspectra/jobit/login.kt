package com.hackspectra.jobit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var loginBtn: Button
    private lateinit var signupBtn: Button  // Add this line

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Firebase Auth instance
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        emailET = findViewById(R.id.etEmail)
        passwordET = findViewById(R.id.etPassword)
        loginBtn = findViewById(R.id.btnLogin)
        signupBtn = findViewById(R.id.btnGoToSignup)  // Initialize Sign Up button

        // Login Button Click
        loginBtn.setOnClickListener {
            val email = emailET.text.toString().trim()
            val password = passwordET.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        // Sign Up Button Click
        signupBtn.setOnClickListener {
            startActivity(Intent(this, signup::class.java))
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val safeEmail = email.replace(".", ",").replace("@", "_at_")

                        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        firestore.collection("users").document(safeEmail).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // Data exists → go to HomeActivity
                                    startActivity(Intent(this@login, Home_Activity::class.java))
                                    finish()
                                } else {
                                    // No data → go to Profile creation screen
                                    startActivity(Intent(this@login, userInputData::class.java))
                                    finish()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to check profile: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}