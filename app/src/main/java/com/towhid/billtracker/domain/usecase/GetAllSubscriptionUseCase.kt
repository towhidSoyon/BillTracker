package com.towhid.billtracker.domain.usecase

import com.towhid.billtracker.domain.model.Subscription
import com.towhid.billtracker.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow

class GetAllSubscriptionsUseCase(private val repo: SubscriptionRepository) {
    operator fun invoke(): Flow<List<Subscription>> = repo.observeAll()
}