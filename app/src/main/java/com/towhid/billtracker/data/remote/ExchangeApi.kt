package com.towhid.billtracker.data.remote

import com.towhid.billtracker.data.remote.dto.LatestResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeApi {
    @GET("latest")
    suspend fun latest(@Query("base") base: String = "USD"): LatestResponseDto
}