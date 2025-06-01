package com.merkost.suby.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
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
import com.merkost.suby.model.room.entity.ServiceDb
import com.merkost.suby.model.room.entity.SubscriptionDb

@Database(
    entities = [
        ServiceDb::class,
        CategoryDb::class,
        SubscriptionDb::class,
        PartialSubscriptionDb::class,
        CurrencyRatesDb::class
    ],
    version = 7, exportSchema = true
)
@TypeConverters(CurrencyRatesTypeConverter::class, Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun currencyRatesDao(): CurrencyRatesDao
    abstract fun servicesDao(): ServiceDao
    abstract fun categoryDao(): CategoryDao
}