package com.example.legalapp.api

import com.example.legalapp.models.LegalCase
import retrofit2.http.GET
import retrofit2.http.Query

interface CourtApiService {
    @GET("api/search")
    suspend fun searchCases(
        @Query("q") query: String
    ): List<LegalCase>
} 