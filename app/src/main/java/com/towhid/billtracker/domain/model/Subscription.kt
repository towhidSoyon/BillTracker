package com.towhid.billtracker.domain.model

import java.time.LocalDate

data class Subscription(
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val currency: String,
    val cycle: BillingCycleType,
    val customDays: Int? = null,
    val nextDue: LocalDate,
    val lastPaid: LocalDate? = null,
    val notes: String = ""
)

enum class BillingCycleType { WEEKLY, MONTHLY, YEARLY, CUSTOM }