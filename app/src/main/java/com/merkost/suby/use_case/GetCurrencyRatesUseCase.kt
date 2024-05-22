package com.merkost.suby.use_case

import com.merkost.suby.model.Period
import com.merkost.suby.model.room.entity.CurrencyRatesDb
import com.merkost.suby.model.room.entity.SubscriptionDb
import com.merkost.suby.now
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.ktor.api.RatesApi
import com.merkost.suby.repository.room.CurrencyRatesRepository
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.toLocalDate
import com.merkost.suby.utils.Constants
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import timber.log.Timber

class GetCurrencyRatesUseCase(
    private val ratesApi: RatesApi,
    appSettings: AppSettings,
    private val subscriptionRepository: SubscriptionRepository,
    private val currencyRatesRepository: CurrencyRatesRepository
) {
    private val mainCurrency = appSettings.mainCurrency

    suspend operator fun invoke(period: Period): Double? {
        val rates = getRates()
        rates?.let {
            return kotlin.runCatching { calculateTotalCost(rates, period) }.onFailure {
                Timber.tag("GetCurrencyRatesUseCase").e(it, "Failed to calculate total cost")
            }.getOrNull()
        }
        return null
    }

    private suspend fun getRates(): CurrencyRatesDb? {
        val mainCurrency = mainCurrency.first()
        val rates = currencyRatesRepository.getRatesByMainCurrency(mainCurrency).first()
        if (rates == null
            || rates.lastUpdated.toLocalDate < LocalDate.now()
                .minus(Constants.CURRENCY_RATES_CACHE_DAYS)
            || rates.mainCurrency != mainCurrency
        ) {
            ratesApi.getCurrencyRates(mainCurrency.code.lowercase()).first().fold(
                onSuccess = { newRates ->
                    val dbRates = newRates.toCurrencyRates(mainCurrency)
                    currencyRatesRepository.addCurrencyRates(dbRates)
                    return dbRates
                },
                onFailure = {
                    Timber.tag("GetCurrencyRatesUseCase")
                        .e(it, "Failed to get currency rates for $mainCurrency")
                    return rates
                }
            )
        }
        return rates
    }

    @Throws
    private suspend fun calculateTotalCost(
        rates: CurrencyRatesDb,
        period: Period
    ): Double {
        val mainCurrency = mainCurrency.first()
        val subscriptions = subscriptionRepository.subscriptions.first()

        // Calculate cost of subscriptions in the main currency
        val subscriptionsInMainCurrency = subscriptions
            .filter { it.currency == mainCurrency }
            .sumOf { calculateSubscriptionCost(it, period) }

        // Calculate cost of subscriptions in other currencies and convert them to the main currency
        val subscriptionsInAnotherCurrency = subscriptions
            .filter { it.currency != mainCurrency }
            .sumOf {
                val rate = rates.rates[it.currency] ?: 1.0
                calculateSubscriptionCost(it, period) / rate
            }

        return subscriptionsInMainCurrency + subscriptionsInAnotherCurrency
    }

    @Throws
    private fun calculateSubscriptionCost(
        subscriptionDb: SubscriptionDb,
        targetPeriod: Period
    ): Double {
        val daysInTargetPeriod = targetPeriod.days
        val daysInSubscriptionPeriod = subscriptionDb.periodDays
        val pricePerDay = subscriptionDb.price / daysInSubscriptionPeriod
        val result = pricePerDay * daysInTargetPeriod

        if (result.isFinite()) return result
        else throw Throwable("Subscription cost calculation error: $subscriptionDb, $targetPeriod")
    }
}
