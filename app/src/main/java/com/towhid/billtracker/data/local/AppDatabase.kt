package com.towhid.billtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.towhid.billtracker.data.local.dao.SubscriptionDao
import com.towhid.billtracker.data.local.entity.SubscriptionEntity

@Database(entities = [SubscriptionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
}