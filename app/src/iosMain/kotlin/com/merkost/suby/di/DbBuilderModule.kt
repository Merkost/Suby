package com.merkost.suby.di

import androidx.room.RoomDatabase
import com.merkost.suby.model.room.AppDatabase
import com.merkost.suby.shared.getDatabaseBuilder
import org.koin.dsl.module

actual val dbBuilderModule = module {
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }
}