package com.towhid.billtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.towhid.billtracker.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY nextDueEpochDay ASC")
    fun observeAll(): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SubscriptionEntity): Long

    @Delete
    suspend fun delete(entity: SubscriptionEntity)

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: Long): SubscriptionEntity?
}