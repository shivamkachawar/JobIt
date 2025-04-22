package com.hackspectra.jobit

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val tvName = view.findViewById<TextView>(R.id.nameText)
        val tvEmail = view.findViewById<TextView>(R.id.emailText)
        val tvPhone = view.findViewById<TextView>(R.id.phoneText)
        val tvJobTitle = view.findViewById<TextView>(R.id.jobTitleText)
        val tvProjects = view.findViewById<TextView>(R.id.projectsText)
        val tvExperience = view.findViewById<TextView>(R.id.experienceText)
        val profileImage = view.findViewById<ImageView>(R.id.profileImage)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val email = user.email ?: ""
            tvEmail.text = email

            // Encode email to match Firestore document ID
            val encodedEmail = email.replace("@", "_at_").replace(".", ",")

            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(encodedEmail).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        tvName.text = document.getString("name") ?: "N/A"
                        tvPhone.text = document.getString("phone") ?: "N/A"
                        tvJobTitle.text = document.getString("jobTitle") ?: "N/A"
                        tvProjects.text = document.getString("projects") ?: "N/A"
                        tvExperience.text = document.getString("experiences") ?: "N/A"

                        val imageUrl = document.getString("photoUrl")
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(imageUrl)
                                .circleCrop()
                                .into(profileImage)
                        }
                    } else {
                        tvName.text = "Profile not found"
                    }
                }
                .addOnFailureListener { e ->
                    tvName.text = "Error fetching data"
                    e.printStackTrace()
                }

        } else {
            tvEmail.text = "Not logged in"
        }

        // Logout button
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            // Redirect to LoginActivity
            val intent = Intent(requireActivity(), login::class.java)
            startActivity(intent)

            // Finish current activity so user can’t come back via back button
            requireActivity().finish()
        }

        // Edit button
        val editButton = view.findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            // Navigate to UserInputDataActivity for editing profile
            val intent = Intent(requireActivity(), userInputData::class.java)
            startActivity(intent)
        }

        return view
    }


}