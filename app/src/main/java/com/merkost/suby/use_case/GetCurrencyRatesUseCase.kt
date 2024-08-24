package com.merkost.suby.use_case

import com.merkost.suby.model.entity.Period
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.model.room.entity.CurrencyRatesDb
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.ktor.api.RatesApi
import com.merkost.suby.repository.room.CurrencyRatesRepository
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.utils.Constants
import com.merkost.suby.utils.now
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
        val rates = getRates() ?: return null
        return kotlin.runCatching { calculateTotalCost(rates, period) }.onFailure {
            Timber.tag("GetCurrencyRatesUseCase").e(it, "Failed to calculate total cost")
        }.getOrNull()
    }

    private suspend fun getRates(): CurrencyRatesDb? {
        val mainCurrency = mainCurrency.first()
        val rates = currencyRatesRepository.getRatesByMainCurrency(mainCurrency).first()
        if (rates == null
            || rates.lastUpdated < LocalDate.now()
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
        subscription: Subscription,
        targetPeriod: Period
    ): Double {
        val daysInSubscriptionPeriod = subscription.period.toApproximateDays()
        val daysInTargetPeriod = targetPeriod.days
        if (daysInSubscriptionPeriod <= 0) {
            throw IllegalArgumentException("Invalid subscription period duration: $daysInSubscriptionPeriod")
        }
        val pricePerDay = subscription.price / daysInSubscriptionPeriod
        val result = pricePerDay * daysInTargetPeriod
        if (result.isFinite()) return result
        else throw IllegalStateException("Subscription cost calculation error: $subscription, $targetPeriod")
    }
}
