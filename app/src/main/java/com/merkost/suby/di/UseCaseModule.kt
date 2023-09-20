package com.merkost.suby.di

import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.ktor.api.RatesApi
import com.merkost.suby.repository.room.CurrencyRatesRepository
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.viewModel.GetCurrencyRatesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetCurrencyRatesUseCase(
        appSettings: AppSettings,
        ratesApi: RatesApi,
        currencyRatesRepository: CurrencyRatesRepository,
        subscriptionRepository: SubscriptionRepository
    ): GetCurrencyRatesUseCase {
        return GetCurrencyRatesUseCase(
            ratesApi = ratesApi,
            appSettings = appSettings,
            currencyRatesRepository = currencyRatesRepository,
            subscriptionRepository = subscriptionRepository
        )
    }
}