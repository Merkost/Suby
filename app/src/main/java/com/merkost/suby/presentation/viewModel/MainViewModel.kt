package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.domain.CurrencyFormat
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Period
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.presentation.FilterOption
import com.merkost.suby.presentation.FilterOption.ACTIVE
import com.merkost.suby.presentation.FilterOption.ALL
import com.merkost.suby.presentation.FilterOption.CANCELLED
import com.merkost.suby.presentation.FilterOption.EXPIRED
import com.merkost.suby.presentation.FilterOption.TRIAL
import com.merkost.suby.presentation.SelectedSortState
import com.merkost.suby.presentation.SortDirection
import com.merkost.suby.presentation.SortOption
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.datastore.LastTotalPrice
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.roundToBigDecimal
import com.merkost.suby.use_case.GetCurrencyRatesUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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


class MainViewModel(
    subscriptionRepository: SubscriptionRepository,
    private val appSettings: AppSettings,
    private val getCurrencyRatesUseCase: GetCurrencyRatesUseCase,
    private val currencyFormatter: CurrencyFormat,
) : ViewModel() {

    val subscriptions = subscriptionRepository.subscriptions
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val mainCurrency = appSettings.mainCurrency
        .stateIn(viewModelScope, SharingStarted.Eagerly, Currency.USD)

    val period = MutableStateFlow(Period.MONTHLY)

    private val _sortState =
        MutableStateFlow(SelectedSortState(SortOption.PAYMENT_DATE, SortDirection.ASCENDING))
    val sortState: StateFlow<SelectedSortState> = _sortState

    private val _selectedFilters = MutableStateFlow<List<FilterOption>>(listOf(ALL))
    val selectedFilters: StateFlow<List<FilterOption>> = _selectedFilters

    val filteredAndSortedSubscriptions: StateFlow<List<Subscription>> = combine(
        subscriptions,
        sortState,
        selectedFilters
    ) { subscriptions, sortState, selectedFilters ->
        var filteredSubscriptions = subscriptions

        if (selectedFilters.isNotEmpty() && !selectedFilters.contains(ALL)) {
            filteredSubscriptions = filteredSubscriptions.filter { subscription ->
                selectedFilters.any { filter ->
                    when (filter) {
                        ACTIVE -> subscription.status == Status.ACTIVE
                        EXPIRED -> subscription.status == Status.EXPIRED
                        CANCELLED -> subscription.status == Status.CANCELED
                        TRIAL -> subscription.status == Status.TRIAL
                        ALL -> true
                    }
                }
            }
        }

        filteredSubscriptions = when (sortState.selectedOption) {
            SortOption.NAME -> {
                if (sortState.direction == SortDirection.ASCENDING) {
                    filteredSubscriptions.sortedBy { it.serviceName }
                } else {
                    filteredSubscriptions.sortedByDescending { it.serviceName }
                }
            }

            SortOption.PRICE -> {
                if (sortState.direction == SortDirection.ASCENDING) {
                    filteredSubscriptions.sortedBy { it.price }
                } else {
                    filteredSubscriptions.sortedByDescending { it.price }
                }
            }

            SortOption.PAYMENT_DATE -> {
                filteredSubscriptions.sortedBy { it.remainingDays }
                    .let {
                        if (sortState.direction == SortDirection.DESCENDING) it.reversed() else it
                    }

            }

            SortOption.STATUS -> {
                if (sortState.direction == SortDirection.ASCENDING) {
                    filteredSubscriptions.sortedBy { it.status }
                } else {
                    filteredSubscriptions.sortedByDescending { it.status }
                }
            }
        }

        filteredSubscriptions
    }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

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
                total = lastTotal?.totalPrice?.formatWithCurrency(currencyFormatter, currency.code),
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
                        total = lastTotal.totalPrice.formatWithCurrency(
                            currencyFormatter,
                            currency.code
                        ),
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
                    total = currencyResult.formatWithCurrency(currencyFormatter, currency.code),
                    lastUpdated = java.time.LocalDateTime.now().toKotlinLocalDateTime()
                )
            }

            appSettings.saveLastTotalPrice(
                LastTotalPrice(
                    totalPrice = currencyResult,
                    currency = currency,
                    lastUpdated = java.time.LocalDateTime.now().toKotlinLocalDateTime()
                )
            )
        }
    }

    fun toggleFilter(filter: FilterOption) {
        viewModelScope.launch {
            _selectedFilters.value = if (filter == ALL) {
                listOf(ALL)
            } else {
                val currentFilters = _selectedFilters.value
                val updatedFilters = if (currentFilters.contains(filter)) {
                    currentFilters - filter
                } else {
                    currentFilters + filter
                }

                if (updatedFilters.isEmpty()) {
                    listOf(ALL)
                } else {
                    updatedFilters.filter { it != ALL }
                }
            }
        }
    }


    fun selectSortOption(sortOption: SortOption) {
        viewModelScope.launch {
            if (_sortState.value.selectedOption == sortOption) {
                // Toggle between ASCENDING and DESCENDING
                _sortState.value = _sortState.value.copy(
                    direction = if (_sortState.value.direction == SortDirection.ASCENDING)
                        SortDirection.DESCENDING else SortDirection.ASCENDING
                )
            } else {
                // Select a new sort option and default to ASCENDING
                _sortState.value = SelectedSortState(sortOption, SortDirection.ASCENDING)
            }
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

    fun onFiltersReset() {
        _selectedFilters.update { listOf(ALL) }
    }
}

private fun Double?.formatWithCurrency(
    currencyFormatter: CurrencyFormat,
    currencyCode: String
): String? {
    return this?.let {
        currencyFormatter.formatCurrencyStyle(it.roundToBigDecimal(), currencyCode)
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
