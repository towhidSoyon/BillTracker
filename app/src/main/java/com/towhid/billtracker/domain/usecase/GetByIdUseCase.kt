package com.towhid.billtracker.domain.usecase

import com.towhid.billtracker.domain.model.Subscription
import com.towhid.billtracker.domain.repository.SubscriptionRepository

class GetByIdUseCase(private val repo: SubscriptionRepository){
    suspend operator fun invoke(id: Long): Subscription? = repo.getById(id)
}