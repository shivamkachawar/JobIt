package com.hackspectra.jobit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://jsearch.p.rapidapi.com/"

    // 1. Create a logging interceptor
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Logs request and response body
    }

    // 2. Add it to your OkHttpClient
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging) // <-- Add this
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-RapidAPI-Key", "dcd1bdc017msh570ba3f90883542p1aac17jsn86e7052acc7e")
                .addHeader("X-RapidAPI-Host", "jsearch.p.rapidapi.com")
                .build()
            chain.proceed(request)
        }
        .build()

    // 3. Build Retrofit using this client
    val api: JobApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JobApiService::class.java)
    }
}