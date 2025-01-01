package com.merkost.suby.di

import com.merkost.suby.use_case.GetCurrencyRatesUseCase
import com.merkost.suby.use_case.GetServicesUseCase
import com.merkost.suby.utils.ImageFileManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factory<ImageFileManager> { ImageFileManager(androidContext()) }
    factoryOf(::GetCurrencyRatesUseCase)
    factoryOf(::GetServicesUseCase)

}