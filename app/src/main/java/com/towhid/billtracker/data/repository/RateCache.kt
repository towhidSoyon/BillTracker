package com.towhid.billtracker.data.repository

import com.towhid.billtracker.data.remote.ExchangeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

data class RatesCache(val base: String, val date: String, val rates: Map<String, Double>)

class RateCache(private val api: ExchangeApi) {
    private val _cache = AtomicReference<RatesCache?>(null)

    suspend fun refresh(base: String) = withContext(Dispatchers.IO) {
        val res = api.latest(base)
        val cache = RatesCache(res.base, res.date, res.rates)
        _cache.set(cache)
        cache
    }

    fun get(): RatesCache? = _cache.get()
}