package com.towhid.billtracker.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.towhid.billtracker.data.local.dao.SubscriptionDao
import com.towhid.billtracker.data.mapper.SubscriptionMapper
import com.towhid.billtracker.domain.model.Subscription
import com.towhid.billtracker.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubscriptionRepositoryImpl(private val dao: SubscriptionDao) : SubscriptionRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeAll(): Flow<List<Subscription>> = dao.observeAll().map { list -> list.map { SubscriptionMapper.toDomain(it) } }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getById(id: Long): Subscription? = dao.getById(id)?.let { SubscriptionMapper.toDomain(it) }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun upsert(subscription: Subscription): Long = dao.upsert(SubscriptionMapper.toEntity(subscription))

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun delete(subscription: Subscription) = dao.delete(SubscriptionMapper.toEntity(subscription))
}