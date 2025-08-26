package com.towhid.billtracker.domain.usecase

import com.towhid.billtracker.domain.model.Subscription
import com.towhid.billtracker.domain.repository.SubscriptionRepository

class DeleteSubscriptionUseCase(private val repo: SubscriptionRepository) {
    suspend operator fun invoke(item: Subscription) = repo.delete(item)
}