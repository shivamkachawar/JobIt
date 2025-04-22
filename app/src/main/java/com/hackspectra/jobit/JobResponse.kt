package com.hackspectra.jobit

import com.google.gson.annotations.SerializedName

data class JobResponse(
    @SerializedName("data")
    val data: List<JobData>
)

data class JobData(
    val job_title: String?,
    val employer_name: String?,
    val job_city: String?,
    val job_description: String?,
    val job_apply_link : String?
)