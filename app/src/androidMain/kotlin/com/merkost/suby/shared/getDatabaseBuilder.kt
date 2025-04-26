package com.merkost.suby.shared

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.merkost.suby.BuildConfig
import com.merkost.suby.model.room.AppDatabase

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(
        if (BuildConfig.DEBUG) "app_database_debug.db" else "app_database.db"
    )
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}