package com.towhid.billtracker.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LatestResponseDto(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)