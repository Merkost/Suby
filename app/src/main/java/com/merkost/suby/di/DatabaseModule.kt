package com.merkost.suby.di

import android.content.Context
import androidx.room.Room
import com.merkost.suby.model.room.AppDatabase
import com.merkost.suby.model.room.dao.CategoryDao
import com.merkost.suby.model.room.dao.CurrencyRatesDao
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.model.room.dao.SubscriptionDao
import com.merkost.suby.repository.room.CurrencyRatesRepository
import com.merkost.suby.repository.room.CurrencyRatesRepositoryImpl
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.repository.room.SubscriptionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Provides
    @Singleton
    fun provideServiceDao(appDatabase: AppDatabase): ServiceDao {
        return appDatabase.servicesDao()
    }

    @Provides
    @Singleton
    fun provideSubscriptionDao(appDatabase: AppDatabase): SubscriptionDao {
        return appDatabase.subscriptionDao()
    }

    @Provides
    @Singleton
    fun provideCurrencyRatesDao(appDatabase: AppDatabase): CurrencyRatesDao {
        return appDatabase.currencyRatesDao()
    }

    @Provides
    @Singleton
    fun provideSubscriptionRepository(subscriptionDao: SubscriptionDao): SubscriptionRepository {
        return SubscriptionRepositoryImpl(subscriptionDao)
    }

    @Provides
    @Singleton
    fun provideCurrencyRatesRepository(currencyRatesDao: CurrencyRatesDao): CurrencyRatesRepository {
        return CurrencyRatesRepositoryImpl(currencyRatesDao)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()

        // TODO: Remove fallbackToDestructiveMigration

    }
}
