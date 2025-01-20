package com.merkost.suby.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.merkost.suby.BuildConfig
import com.merkost.suby.di.Migrations.MIGRATION_1_2
import com.merkost.suby.di.Migrations.MIGRATION_2_3
import com.merkost.suby.di.Migrations.MIGRATION_3_4
import com.merkost.suby.di.Migrations.MIGRATION_4_5
import com.merkost.suby.model.room.AppDatabase
import com.merkost.suby.model.room.dao.CategoryDao
import com.merkost.suby.model.room.dao.CurrencyRatesDao
import com.merkost.suby.model.room.dao.ServiceDao
import com.merkost.suby.model.room.dao.SubscriptionDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {

    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            if (BuildConfig.DEBUG) "app_database_debug.db" else "app_database.db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .build()
    }

    single<CategoryDao> { get<AppDatabase>().categoryDao() }
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

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val MAX_BACKEND_SERVICE_ID = 200

            db.execSQL("""
            CREATE TABLE IF NOT EXISTS service_new (
                id INTEGER PRIMARY KEY NOT NULL,
                backendId INTEGER,
                name TEXT NOT NULL,
                categoryId INTEGER NOT NULL,
                logoName TEXT,
                customImageUri TEXT,
                isDeprecated INTEGER NOT NULL DEFAULT 0,
                createdAt TEXT NOT NULL,
                lastUpdated TEXT NOT NULL,
                FOREIGN KEY(categoryId) REFERENCES category(id) ON DELETE CASCADE
            )
        """.trimIndent())

            db.execSQL("""
            INSERT INTO service_new (id, backendId, name, categoryId, logoName, customImageUri, isDeprecated, createdAt, lastUpdated)
            SELECT id, id AS backendId, name, categoryId, logoName, NULL, 0, createdAt, lastUpdated
            FROM service
        """.trimIndent())

            db.execSQL("""
            INSERT INTO service_new (id, backendId, name, categoryId, logoName, customImageUri, isDeprecated, createdAt, lastUpdated)
            SELECT id + $MAX_BACKEND_SERVICE_ID, NULL, name, categoryId, NULL, imageUri, 0, createdAt, lastUpdated
            FROM custom_service
        """.trimIndent())

            // 4. Update subscriptions for custom services by adding the offset to serviceId where isCustomService = 1
            db.execSQL("""
            UPDATE subscription
            SET serviceId = serviceId + $MAX_BACKEND_SERVICE_ID
            WHERE isCustomService = 1
        """.trimIndent())

            db.execSQL("DROP TABLE service")
            db.execSQL("DROP TABLE custom_service")

            db.execSQL("ALTER TABLE service_new RENAME TO service")

            db.execSQL("CREATE INDEX IF NOT EXISTS index_service_categoryId ON service(categoryId)")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_service_backendId ON service(backendId)")

            db.execSQL("""
            CREATE TABLE IF NOT EXISTS subscription_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                serviceId INTEGER NOT NULL,
                price REAL NOT NULL,
                currency TEXT NOT NULL,
                periodType TEXT NOT NULL,
                periodDuration INTEGER NOT NULL,
                status TEXT NOT NULL,
                paymentDate TEXT NOT NULL,
                createdDate TEXT NOT NULL,
                description TEXT NOT NULL,
                FOREIGN KEY(serviceId) REFERENCES service(id) ON DELETE CASCADE
            )
        """.trimIndent())

            db.execSQL("""
            INSERT INTO subscription_new (id, serviceId, price, currency, periodType, periodDuration, status, paymentDate, createdDate, description)
            SELECT id, serviceId, price, currency, periodType, periodDuration, status, paymentDate, createdDate, description
            FROM subscription
        """.trimIndent())

            db.execSQL("DROP TABLE subscription")

            db.execSQL("ALTER TABLE subscription_new RENAME TO subscription")

            db.execSQL("CREATE INDEX IF NOT EXISTS index_subscription_serviceId ON subscription(serviceId)")
        }
    }

}
