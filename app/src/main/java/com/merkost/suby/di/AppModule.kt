package com.merkost.suby.di

import com.amplitude.android.Amplitude
import com.amplitude.android.AutocaptureOption
import com.amplitude.android.Configuration
import com.merkost.suby.BuildConfig
import com.merkost.suby.domain.CurrencyFormat
import com.merkost.suby.domain.CurrencyFormatImpl
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.datastore.AppSettingsImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.Locale

val appModule = module {

    single<Amplitude> {
        Amplitude(
            Configuration(
                apiKey = BuildConfig.AMPLITUDE_API_KEY,
                context = get(),
                autocapture = AutocaptureOption.entries.toSet(),
            )
        )
    }

    factory<Locale> { Locale.getDefault() }
    factoryOf(::CurrencyFormatImpl) bind CurrencyFormat::class
    single<AppSettings> { AppSettingsImpl(get()) }
}