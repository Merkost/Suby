package com.merkost.suby.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.formatDecimal
import com.merkost.suby.model.Currency
import com.merkost.suby.model.Period
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.datastore.LastTotalPrice
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.use_case.GetCurrencyRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    subscriptionRepository: SubscriptionRepository,
    private val appSettings: AppSettings,
    private val getCurrencyRatesUseCase: GetCurrencyRatesUseCase
) : ViewModel() {

    val subscriptions = subscriptionRepository.subscriptions
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val mainCurrency = appSettings.mainCurrency
        .stateIn(viewModelScope, SharingStarted.Eagerly, Currency.USD)

    val period = MutableStateFlow(Period.MONTHLY)

    private val _total = MutableStateFlow(TotalPrice())
    val total = _total.asStateFlow()

    private val combinedFlow = combine(
        subscriptionRepository.subscriptions,
        appSettings.mainCurrency,
        period
    ) { subscriptions, currency, period ->
        Triple(subscriptions, currency, period)
    }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            combinedFlow.collectLatest {
                updateRates()
            }
        }
    }

    private suspend fun updateRates() {
        Timber.tag("MainViewModel").d("Updating rates")
        val lastTotal = appSettings.lastTotalPrice.first()
        val currency = mainCurrency.first()

        _total.update {
            it.copy(
                isUpdating = true,
                isLoading = true,
                currency = currency,
                total = lastTotal?.totalPrice?.formatDecimal(),
            )
        }

        val currencyResult = getCurrencyRatesUseCase(period.value)

        delay(2000)

        if (currencyResult == null) {
            if (currency == lastTotal?.currency) {
                _total.update {
                    it.copy(
                        isLoading = false,
                        isUpdating = false,
                        total = lastTotal.totalPrice.formatDecimal(),
                        lastUpdated = lastTotal.lastUpdated
                    )
                }
            } else {
                _total.update {
                    it.copy(
                        isLoading = false,
                        isUpdating = false,
                        total = null,
                        lastUpdated = null
                    )
                }
                return
            }
        } else {
            _total.update {
                it.copy(
                    isLoading = false,
                    isUpdating = false,
                    total = currencyResult.formatDecimal(),
                    lastUpdated = java.time.LocalDateTime.now().toKotlinLocalDateTime()
                )
            }

            appSettings.saveLastTotalPrice(
                LastTotalPrice(
                    currencyResult,
                    currency,
                    java.time.LocalDateTime.now().toKotlinLocalDateTime()
                )
            )
        }
    }

    fun onUpdateRatesClicked() {
        viewModelScope.launch {
            updateRates()
        }
    }

    fun updateMainCurrency(currency: Currency) {
        viewModelScope.launch {
            appSettings.saveMainCurrency(currency)
        }
    }

    fun updateMainPeriod() {
        period.update { period.value.nextMain() }
    }
}


// TODO: Specify default period in onBoarding
data class TotalPrice(
    val isLoading: Boolean = true,
    val isUpdating: Boolean = true,
    val total: String? = null,
    val currency: Currency = Currency.USD,
    val lastUpdated: LocalDateTime? = null
)
