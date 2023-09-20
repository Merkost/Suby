package com.merkost.suby.viewModel

import com.merkost.suby.model.Period
import com.merkost.suby.model.room.entity.CurrencyRates
import com.merkost.suby.model.room.entity.Subscription
import com.merkost.suby.now
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.ktor.api.RatesApi
import com.merkost.suby.repository.room.CurrencyRatesRepository
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.toLocalDate
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate


class GetCurrencyRatesUseCase(
    private val ratesApi: RatesApi,
    appSettings: AppSettings,
    private val subscriptionRepository: SubscriptionRepository,
    private val currencyRatesRepository: CurrencyRatesRepository
) {
    private val mainCurrency = appSettings.mainCurrency

    suspend operator fun invoke(period: Period): Pair<Period, Double> {
        val rates = getRates()
        return calculateTotalCost(rates, period)
    }

    private suspend fun getRates(): CurrencyRates {
        val mainCurrency = mainCurrency.first()
        val rates = currencyRatesRepository.getRatesByMainCurrency(mainCurrency).first()
        if (rates == null || rates.lastUpdated.toLocalDate == LocalDate.now()) {
            val newRates =
                ratesApi.getCurrencyRates(mainCurrency.code.lowercase()).first().getOrNull()
            // TODO:  
            val dbRates = newRates!!.toCurrencyRates(mainCurrency)
            currencyRatesRepository.addCurrencyRates(dbRates)
            return dbRates
        }
        return rates
    }

    private suspend fun calculateTotalCost(
        rates: CurrencyRates,
        period: Period
    ): Pair<Period, Double> {
        val mainCurrency = mainCurrency.first()
        val subscriptions = subscriptionRepository.subscriptions.first()
        val subscriptionsInMainCurrency = subscriptions
            .filter { it.currency == mainCurrency }
            .sumOf(period)
        val subscriptionsInAnotherCurrency = subscriptions
            .filter { it.currency != mainCurrency }
            .map {
                it.copy(price = it.price / rates.rates[it.currency]!!.toDouble())
            }.sumOf(period)

        return period to subscriptionsInMainCurrency + subscriptionsInAnotherCurrency
    }
}

private fun List<Subscription>.sumOf(period: Period): Double {
    val currentPeriod = this.filter { it.period == period }
    val otherPeriods = this.filter { it.period != period }
    return currentPeriod.sumOf { it.price } + otherPeriods.map { it.price / it.period.days * period.days }
        .sum()
}
