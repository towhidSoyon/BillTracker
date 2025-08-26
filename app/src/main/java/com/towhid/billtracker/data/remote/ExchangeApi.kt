package com.towhid.billtracker.data.remote

import com.towhid.billtracker.data.remote.dto.ConversionResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeApi {

    @GET("convert")
    suspend fun convert(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): Response<ConversionResponseDto>
}