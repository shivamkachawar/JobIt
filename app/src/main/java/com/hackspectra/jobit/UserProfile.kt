package com.hackspectra.jobit

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val jobTitle: String = "",
    val projects: String = "",
    val experiences: String = "",
    val photoUrl: String = "",
    val resumeUrl: String = ""
)