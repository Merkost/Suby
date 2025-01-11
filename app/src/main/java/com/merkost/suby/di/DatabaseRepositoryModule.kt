package com.merkost.suby.di

import com.merkost.suby.repository.datastore.AppStateRepository
import com.merkost.suby.repository.room.CurrencyRatesRepository
import com.merkost.suby.repository.room.CurrencyRatesRepositoryImpl
import com.merkost.suby.repository.room.ServiceRepository
import com.merkost.suby.repository.room.ServiceRepositoryImpl
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.repository.room.SubscriptionRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseRepositoryModule = module {
    factoryOf(::ServiceRepositoryImpl) bind ServiceRepository::class
    factoryOf(::SubscriptionRepositoryImpl) bind SubscriptionRepository::class
    factoryOf(::CurrencyRatesRepositoryImpl) bind CurrencyRatesRepository::class

    single { AppStateRepository(get(), get(), get()) }
}
