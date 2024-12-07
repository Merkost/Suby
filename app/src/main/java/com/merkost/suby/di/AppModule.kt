package com.merkost.suby.di

import android.content.Context
import com.merkost.suby.domain.CurrencyFormat
import com.merkost.suby.domain.CurrencyFormatImpl
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.datastore.AppSettingsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideCurrencyFormat(@ApplicationContext context: Context): CurrencyFormat {
        return CurrencyFormatImpl(context.resources.configuration.locales[0])
    }

    @Singleton
    @Provides
    fun provideAppSettings(@ApplicationContext context: Context): AppSettings {
        return AppSettingsImpl(context)
    }

}