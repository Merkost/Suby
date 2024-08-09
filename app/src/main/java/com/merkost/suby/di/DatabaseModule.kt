package com.merkost.suby.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.merkost.suby.di.Migrations.MIGRATION_1_2
import com.merkost.suby.di.Migrations.MIGRATION_2_3
import com.merkost.suby.model.room.AppDatabase
import com.merkost.suby.model.room.dao.CategoryDao
import com.merkost.suby.model.room.dao.CurrencyRatesDao
import com.merkost.suby.model.room.dao.CustomServiceDao
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.model.room.dao.SubscriptionDao
import com.merkost.suby.repository.room.CurrencyRatesRepository
import com.merkost.suby.repository.room.CurrencyRatesRepositoryImpl
import com.merkost.suby.repository.room.ServiceRepository
import com.merkost.suby.repository.room.ServiceRepositoryImpl
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
    fun provideCustomServiceDao(appDatabase: AppDatabase): CustomServiceDao {
        return appDatabase.customServiceDao()
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
    fun provideServicesRepository(
        serviceDao: ServiceDao, customServiceDao: CustomServiceDao
    ): ServiceRepository {
        return ServiceRepositoryImpl(serviceDao, customServiceDao)
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
            appContext, AppDatabase::class.java, "app_database.db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

        // TODO: Remove fallbackToDestructiveMigration

    }
}

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS service_new (
                id INTEGER PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                categoryId INTEGER NOT NULL,
                logoName TEXT,
                createdAt TEXT NOT NULL,
                lastUpdated TEXT NOT NULL,
                FOREIGN KEY(categoryId) REFERENCES category(id) ON DELETE CASCADE
            )
        """.trimIndent())

            db.execSQL("""
            INSERT INTO service_new (id, name, categoryId, createdAt, lastUpdated)
            SELECT id, name, categoryId, createdAt, lastUpdated FROM service
        """.trimIndent())

            db.execSQL("DROP TABLE service")

            db.execSQL("ALTER TABLE service_new RENAME TO service")

            db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_service_categoryId ON service(categoryId)
        """.trimIndent())
        }
    }

    val MIGRATION_2_3 = object : Migration(2,3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE custom_service ADD COLUMN imageUri TEXT")
        }
    }
}
