package com.merkost.suby.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.merkost.suby.di.Migrations.MIGRATION_1_2
import com.merkost.suby.di.Migrations.MIGRATION_2_3
import com.merkost.suby.di.Migrations.MIGRATION_3_4
import com.merkost.suby.model.room.AppDatabase
import com.merkost.suby.model.room.dao.CategoryDao
import com.merkost.suby.model.room.dao.CurrencyRatesDao
import com.merkost.suby.model.room.dao.CustomServiceDao
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.model.room.dao.SubscriptionDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {

    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "app_database.db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
    }

    single<CategoryDao> { get<AppDatabase>().categoryDao() }
    single<CustomServiceDao> { get<AppDatabase>().customServiceDao() }
    single<ServiceDao> { get<AppDatabase>().servicesDao() }
    single<SubscriptionDao> { get<AppDatabase>().subscriptionDao() }
    single<CurrencyRatesDao> { get<AppDatabase>().currencyRatesDao() }

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

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS subscription_new (
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
                description TEXT NOT NULL
            )
        """.trimIndent()
            )

            db.execSQL(
                """
            INSERT INTO subscription_new (id, serviceId, isCustomService, price, currency, 
                                          periodType, periodDuration, status, paymentDate, createdDate, description)
            SELECT id, serviceId, isCustomService, price, currency, 
                   periodType, periodDuration, status, paymentDate, createdDate, description
            FROM subscription
        """.trimIndent()
            )

            db.execSQL("DROP TABLE subscription")

            db.execSQL("ALTER TABLE subscription_new RENAME TO subscription")


            // Create the new table for PartialSubscriptionDb
            db.execSQL(
                """
            CREATE TABLE IF NOT EXISTS PartialSubscriptionDb (
                id INTEGER PRIMARY KEY NOT NULL,
                price REAL NOT NULL,
                currency TEXT NOT NULL,
                periodType TEXT NOT NULL,
                periodDuration INTEGER NOT NULL,
                status TEXT NOT NULL,
                description TEXT NOT NULL,
                paymentDate TEXT NOT NULL
            )
            """.trimIndent()
            )
        }
    }

}
