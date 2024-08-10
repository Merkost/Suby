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
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS service_new (
                id INTEGER PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                categoryId INTEGER NOT NULL,
                logoName TEXT,
                createdAt TEXT NOT NULL,
                lastUpdated TEXT NOT NULL,
                FOREIGN KEY(categoryId) REFERENCES category(id) ON DELETE CASCADE
            )
        """.trimIndent()
            )

            db.execSQL(
                """
            INSERT INTO service_new (id, name, categoryId, createdAt, lastUpdated)
            SELECT id, name, categoryId, createdAt, lastUpdated FROM service
        """.trimIndent()
            )

            db.execSQL("DROP TABLE service")

            db.execSQL("ALTER TABLE service_new RENAME TO service")

            db.execSQL(
                """
            CREATE INDEX IF NOT EXISTS index_service_categoryId ON service(categoryId)
        """.trimIndent()
            )
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE custom_service ADD COLUMN imageUri TEXT")

            db.execSQL("ALTER TABLE subscription ADD COLUMN periodType TEXT NOT NULL DEFAULT 'DAYS'")
            db.execSQL("ALTER TABLE subscription ADD COLUMN periodDuration INTEGER NOT NULL DEFAULT 1")

            db.execSQL(
                """
            UPDATE subscription SET periodType = 
            CASE period
                WHEN 'DAILY' THEN 'DAYS'
                WHEN 'WEEKLY' THEN 'WEEKS'
                WHEN 'BI_WEEKLY' THEN 'WEEKS'
                WHEN 'MONTHLY' THEN 'MONTHS'
                WHEN 'QUARTERLY' THEN 'MONTHS'
                WHEN 'SEMI_ANNUAL' THEN 'MONTHS'
                WHEN 'ANNUAL' THEN 'YEARS'
                WHEN 'CUSTOM' THEN customPeriodType
            END
        """
            )

            db.execSQL(
                """
            UPDATE subscription SET periodDuration = 
            CASE period
                WHEN 'DAILY' THEN 1
                WHEN 'WEEKLY' THEN 1
                WHEN 'BI_WEEKLY' THEN 2
                WHEN 'MONTHLY' THEN 1
                WHEN 'QUARTERLY' THEN 3
                WHEN 'SEMI_ANNUAL' THEN 6
                WHEN 'ANNUAL' THEN 1
                WHEN 'CUSTOM' THEN customPeriodDuration
            END
        """
            )

            db.execSQL(
                """
            CREATE TABLE subscription_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                serviceId INTEGER NOT NULL,
                isCustomService INTEGER NOT NULL,
                price REAL NOT NULL,
                currency TEXT NOT NULL,
                periodType TEXT NOT NULL,
                periodDuration INTEGER NOT NULL,
                status TEXT NOT NULL,
                paymentDate TEXT NOT NULL,
                createdDate TEXT NOT NULL,
                durationDays INTEGER NOT NULL,
                description TEXT NOT NULL
            )
        """
            )

            db.execSQL(
                """
            INSERT INTO subscription_new (id, serviceId, isCustomService, price, currency, periodType, periodDuration, status, paymentDate, createdDate, durationDays, description)
            SELECT id, serviceId, isCustomService, price, currency, periodType, periodDuration, status, paymentDate, createdDate, durationDays, description
            FROM subscription
        """
            )

            db.execSQL("DROP TABLE subscription")

            db.execSQL("ALTER TABLE subscription_new RENAME TO subscription")

        }
    }
}
