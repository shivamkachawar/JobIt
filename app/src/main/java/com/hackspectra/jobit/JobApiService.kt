package com.hackspectra.jobit
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface JobApiService {


    @GET("search")
    suspend fun searchJobs(
        @Query("query") keyword: String

    ): Response<JobResponse>
}