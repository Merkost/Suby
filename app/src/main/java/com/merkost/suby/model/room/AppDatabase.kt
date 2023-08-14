package com.merkost.suby.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.merkost.suby.model.room.converter.CurrencyRatesTypeConverter
import com.merkost.suby.model.room.dao.CurrencyRatesDao
import com.merkost.suby.model.room.entity.Subscription
import com.merkost.suby.model.room.dao.SubscriptionDao
import com.merkost.suby.model.room.entity.CurrencyRates

@Database(
    entities = [Subscription::class, CurrencyRates::class],
    version = 2, exportSchema = false
)
@TypeConverters(CurrencyRatesTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun currencyRatesDao(): CurrencyRatesDao
}