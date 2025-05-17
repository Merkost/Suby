package com.merkost.suby.domain.usecase

import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.CustomPeriod
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.model.room.entity.CurrencyRatesDb
import com.merkost.suby.presentation.viewModel.SubscriptionDetailsState
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.room.CurrencyRatesRepository
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.utils.now
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.temporal.ChronoUnit

class SubscriptionStatsUseCase(
    private val subscriptionRepository: SubscriptionRepository,
    private val appSettings: AppSettings,
    private val currencyRatesRepository: CurrencyRatesRepository
) {
    fun calculateSubscriptionStats(subscription: Subscription): Flow<SubscriptionDetailsState> =
        flow {
            val allSubscriptions = subscriptionRepository.subscriptions.first()
            val mainCurrency = appSettings.mainCurrency.first()

            val currencyRates = currencyRatesRepository.getRatesByMainCurrency(mainCurrency).first()
                ?: return@flow

            val categoryId = subscription.category.id
            val categorySubscriptions = allSubscriptions.filter {
                it.category.id == categoryId
            }

            val subscriptionMonthlyPrice = normalizeToMonthlyPrice(subscription)
            val convertedSubscriptionMonthlyPrice = convertToMainCurrency(
                subscriptionMonthlyPrice,
                subscription.currency,
                mainCurrency,
                currencyRates
            )

            val allTotalInMainCurrency = allSubscriptions.sumOf { sub ->
                val normalizedPrice = normalizeToMonthlyPrice(sub)
                convertToMainCurrency(normalizedPrice, sub.currency, mainCurrency, currencyRates)
            }

            val budgetPercentage = if (allTotalInMainCurrency > 0) {
                (convertedSubscriptionMonthlyPrice / allTotalInMainCurrency * 100).toInt()
            } else 0

            val currentDate = LocalDate.now()
            val createdAt = subscription.createdDate.date
            val daysDifference = ChronoUnit.DAYS.between(
                createdAt.toJavaLocalDate(),
                currentDate.toJavaLocalDate()
            ).toInt()

            val rawTotalSpent = calculateTotalSpent(subscription, daysDifference.toLong())
            val totalSpentInMainCurrency = convertToMainCurrency(
                rawTotalSpent,
                subscription.currency,
                mainCurrency,
                currencyRates
            )

            val annualCost = convertedSubscriptionMonthlyPrice * 12

            emit(
                SubscriptionDetailsState(
                    categorySubscriptionsCount = categorySubscriptions.size,
                    totalSubscriptionsCount = allSubscriptions.size,
                    allSubscriptionsMonthlyTotal = allTotalInMainCurrency,
                    subscriptionAgeDays = daysDifference,
                    totalSpentEstimate = totalSpentInMainCurrency,
                    monthlyNormalizedPrice = convertedSubscriptionMonthlyPrice,
                    originalMonthlyPrice = subscriptionMonthlyPrice,
                    budgetPercentage = budgetPercentage,
                    annualCost = annualCost,
                    hasBillingStartDate = true,
                    mainCurrency = mainCurrency
                )
            )
        }

    private fun convertToMainCurrency(
        amount: Double,
        fromCurrency: Currency,
        toCurrency: Currency,
        currencyRates: CurrencyRatesDb
    ): Double {
        if (fromCurrency == toCurrency) return amount

        val rates = currencyRates.rates
        val fromRate = rates[fromCurrency] ?: 1.0
        val toRate = rates[toCurrency] ?: 1.0

        return amount * (1 / fromRate) * toRate
    }

    private fun normalizeToMonthlyPrice(subscription: Subscription): Double {
        return when (subscription.period.type) {
            CustomPeriod.DAYS -> subscription.price * 30 / subscription.period.duration
            CustomPeriod.WEEKS -> subscription.price * 4 / subscription.period.duration
            CustomPeriod.MONTHS -> subscription.price / subscription.period.duration
            CustomPeriod.YEARS -> subscription.price / (subscription.period.duration * 12)
        }
    }

    private fun calculateTotalSpent(subscription: Subscription, daysSinceCreation: Long): Double {
        val paymentFrequencyDays = when (subscription.period.type) {
            CustomPeriod.DAYS -> subscription.period.duration
            CustomPeriod.WEEKS -> subscription.period.duration * 7
            CustomPeriod.MONTHS -> subscription.period.duration * 30
            CustomPeriod.YEARS -> subscription.period.duration * 365
        }

        val numberOfPayments = (daysSinceCreation / paymentFrequencyDays) + 1

        return numberOfPayments * subscription.price
    }
} 