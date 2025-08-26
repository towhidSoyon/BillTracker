package com.towhid.billtracker.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConversionResponseDto(
    val success: Boolean,
    val query: QueryDto,
    val info: InfoDto,
    val result: Double
)

@JsonClass(generateAdapter = true)
data class QueryDto(
    val from: String,
    val to: String,
    val amount: Double
)

@JsonClass(generateAdapter = true)
data class InfoDto(
    val quote: Double
)