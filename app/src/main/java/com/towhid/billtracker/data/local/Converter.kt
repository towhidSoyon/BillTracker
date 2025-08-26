package com.towhid.billtracker.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.towhid.billtracker.domain.model.BillingCycleType
import java.time.LocalDate

class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun epochDayToLocalDate(epochDay: Long?): LocalDate? = epochDay?.let { LocalDate.ofEpochDay(it) }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun fromCycle(type: BillingCycleType): String = type.name

    @TypeConverter
    fun toCycle(value: String): BillingCycleType = BillingCycleType.valueOf(value)
}