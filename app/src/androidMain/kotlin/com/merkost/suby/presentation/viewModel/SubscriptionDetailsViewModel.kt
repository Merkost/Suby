package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.merkost.suby.domain.usecase.SubscriptionStatsUseCase
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.CustomPeriod
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.model.room.entity.CurrencyRatesDb
import com.merkost.suby.presentation.base.BaseUiState
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.utils.Destinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import suby.app.generated.resources.Res
import suby.app.generated.resources.error_subscription_not_found

data class SubscriptionDetailsState(
    val categorySubscriptionsCount: Int = 0,
    val totalSubscriptionsCount: Int = 0,
    val allSubscriptionsMonthlyTotal: Double = 0.0,
    val subscriptionAgeDays: Int = 0,
    val totalSpentEstimate: Double = 0.0,
    val monthlyNormalizedPrice: Double = 0.0,
    val originalMonthlyPrice: Double = 0.0,
    val budgetPercentage: Int = 0,
    val annualCost: Double = 0.0,
    val hasBillingStartDate: Boolean = false,
    val mainCurrency: Currency = Currency.USD
)

class SubscriptionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionStatsUseCase: SubscriptionStatsUseCase
) : ViewModel() {

    val subscriptionId = savedStateHandle.toRoute<Destinations.SubscriptionInfo>().subscriptionId
    val uiState = MutableStateFlow<BaseUiState<Subscription>>(BaseUiState.Loading)

    private val _statsState = MutableStateFlow(SubscriptionDetailsState())
    val statsState: StateFlow<SubscriptionDetailsState> = _statsState.asStateFlow()

    fun deleteSubscription(subscriptionId: Int) {
        viewModelScope.launch {
            subscriptionRepository.removeSubscription(subscriptionId)
        }
    }

    fun loadSubscription() {
        viewModelScope.launch {
            val subscription = subscriptionRepository.getSubscriptionById(subscriptionId).first()

            if (subscription == null) {
                uiState.update {
                    BaseUiState.Error(
                        "Subscription not found",
                        Res.string.error_subscription_not_found
                    )
                }
            } else {
                uiState.update { BaseUiState.Success(subscription) }
                loadStats(subscription)
            }
        }
    }

    private fun loadStats(subscription: Subscription) {
        viewModelScope.launch {
            subscriptionStatsUseCase.calculateSubscriptionStats(subscription).collect { stats ->
                _statsState.update { stats }
            }
        }
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

        val numberOfPayments = daysSinceCreation / paymentFrequencyDays

        return numberOfPayments * subscription.price
    }
}
