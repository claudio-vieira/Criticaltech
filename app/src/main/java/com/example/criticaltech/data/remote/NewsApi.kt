package com.example.criticaltech.data.remote

import com.example.criticaltech.data.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("sources") sources: String,
        @Query("apiKey") apiKey: String = "40c3a2fbf1484d38952b0bedd87f482f"
    ): NewsResponse
}