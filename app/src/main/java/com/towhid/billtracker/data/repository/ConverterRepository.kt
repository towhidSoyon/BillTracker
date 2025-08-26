package com.towhid.billtracker.data.repository

import com.towhid.billtracker.data.remote.ExchangeApi

class ConverterRepository(private val api: ExchangeApi) {

    suspend fun convert(from: String, to: String, amount: Double): Double {
        val res = api.convert(from, to, amount)
        val amount =  res.body()?.result

        return amount?:0.0
    }
}

