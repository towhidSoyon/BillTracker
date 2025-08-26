package com.towhid.billtracker.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.towhid.billtracker.data.local.entity.SubscriptionEntity
import com.towhid.billtracker.domain.model.Subscription
import java.time.LocalDate

object SubscriptionMapper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(e: SubscriptionEntity): Subscription = Subscription(
        id = e.id,
        name = e.name,
        amount = e.amount,
        currency = e.currency,
        cycle = e.cycle,
        customDays = e.customDays,
        nextDue = LocalDate.ofEpochDay(e.nextDueEpochDay),
        lastPaid = e.lastPaidEpochDay?.let { LocalDate.ofEpochDay(it) },
        notes = e.notes
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toEntity(d: Subscription): SubscriptionEntity = SubscriptionEntity(
        id = d.id,
        name = d.name,
        amount = d.amount,
        currency = d.currency,
        cycle = d.cycle,
        customDays = d.customDays,
        nextDueEpochDay = d.nextDue.toEpochDay(),
        lastPaidEpochDay = d.lastPaid?.toEpochDay(),
        notes = d.notes
    )
}