package com.towhid.billtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.towhid.billtracker.domain.model.BillingCycleType

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val currency: String,
    val cycle: BillingCycleType,
    val customDays: Int?,
    val nextDueEpochDay: Long,
    val lastPaidEpochDay: Long?,
    val notes: String
)