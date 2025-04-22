package com.hackspectra.jobit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hackspectra.jobit.JobAdapter
import com.hackspectra.jobit.JobApiService
import com.hackspectra.jobit.JobData
import com.hackspectra.jobit.ProfileFragment
import com.hackspectra.jobit.R
import com.hackspectra.jobit.RetrofitInstance
import kotlinx.coroutines.launch

class homeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var jobAdapter: JobAdapter
    private val jobList = mutableListOf<JobData>()

    private lateinit var apiService: JobApiService
    private lateinit var usernameText: TextView
    private lateinit var profilePhoto: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewJobs)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        jobAdapter = JobAdapter(jobList)
        recyclerView.adapter = jobAdapter

        usernameText = view.findViewById(R.id.usernameText)
        profilePhoto = view.findViewById(R.id.profilePhoto)

        apiService = RetrofitInstance.api

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserInfoAndJobs() // ✅ now it's safe
    }

    private fun fetchUserInfoAndJobs() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail != null) {
            val formattedEmail = userEmail.replace("@", "_at_").replace(".", ",")

            val docRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(formattedEmail)

            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("name")
                    usernameText.text = "Hello, ${username ?: "User"}"

                    val photoUrl = document.getString("photoUrl")
                    if (!photoUrl.isNullOrEmpty()) {
                        // ✅ Use viewLifecycleOwner for safe Glide context
                        if (isAdded) {
                            Glide.with(requireContext())
                                .load(photoUrl)
                                .circleCrop()
                                .placeholder(R.drawable.no_match)
                                .into(profilePhoto)

                            usernameText.text = "Hello, ${username ?: "User"}"
                        }
                    }

                    val jobTitle = document.getString("jobTitle")
                    if (!jobTitle.isNullOrEmpty()) {
                        fetchJobs(jobTitle)
                    } else {
                        Log.e("Firestore", "Job title is empty")
                    }
                } else {
                    Log.e("Firestore", "No such document or missing fields")
                }
            }.addOnFailureListener { exception ->
                Log.e("Firestore", "Failed to fetch user info", exception)
            }
        } else {
            Log.e("Firestore", "User not logged in")
        }
    }

    private fun fetchJobs(keyword: String) {
        lifecycleScope.launch {
            try {
                val response = apiService.searchJobs(keyword)
                if (response.isSuccessful && response.body() != null) {
                    jobList.clear()
                    jobList.addAll(response.body()!!.data)
                    jobAdapter.notifyDataSetChanged()
                } else {
                    Log.e("JobFetch", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("JobFetch", "Exception: ${e.message}")
            }
        }
    }
}
