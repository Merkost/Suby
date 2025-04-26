package com.merkost.suby.model.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.merkost.suby.model.room.converter.Converters
import com.merkost.suby.model.room.converter.CurrencyRatesTypeConverter
import com.merkost.suby.model.room.dao.CategoryDao
import com.merkost.suby.model.room.dao.CurrencyRatesDao
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.model.room.dao.SubscriptionDao
import com.merkost.suby.model.room.entity.CategoryDb
import com.merkost.suby.model.room.entity.CurrencyRatesDb
import com.merkost.suby.model.room.entity.PartialSubscriptionDb
import com.merkost.suby.model.room.entity.Service
import com.merkost.suby.model.room.entity.SubscriptionDb

@Database(
    entities = [
        Service::class,
        CategoryDb::class,
        SubscriptionDb::class,
        PartialSubscriptionDb::class,
        CurrencyRatesDb::class
    ],
    version = 5, exportSchema = true
)
@TypeConverters(CurrencyRatesTypeConverter::class, Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun currencyRatesDao(): CurrencyRatesDao
    abstract fun servicesDao(): ServiceDao
    abstract fun categoryDao(): CategoryDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}