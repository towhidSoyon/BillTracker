package com.towhid.billtracker.domain.repository

import com.towhid.billtracker.domain.model.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun observeAll(): Flow<List<Subscription>>
    suspend fun getById(id: Long): Subscription?
    suspend fun upsert(subscription: Subscription): Long
    suspend fun delete(subscription: Subscription)
}