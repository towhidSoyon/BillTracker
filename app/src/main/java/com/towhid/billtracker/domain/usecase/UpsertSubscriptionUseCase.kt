package com.towhid.billtracker.domain.usecase

import com.towhid.billtracker.domain.model.Subscription
import com.towhid.billtracker.domain.repository.SubscriptionRepository

class UpsertSubscriptionUseCase(private val repo: SubscriptionRepository) {
    suspend operator fun invoke(item: Subscription): Long = repo.upsert(item)
}