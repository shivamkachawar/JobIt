package com.hackspectra.jobit

import com.google.gson.annotations.SerializedName

data class JobResponse(
    @SerializedName("data")
    val data: List<JobData>
)

data class JobData(
    val job_title: String?,
    val publisher_name: String?,
    val location: String?,
    val salary_period: String?
)