package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.Analytics
import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.NewSubscription
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.model.room.entity.SubscriptionDb
import com.merkost.suby.presentation.states.NewSubscriptionUiState
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.utils.toKotlinLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class NewSubscriptionViewModel @Inject constructor(
    appSettings: AppSettings,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewSubscriptionUiState?>(null)
    val uiState = _uiState.asStateFlow()

    val mainCurrency = appSettings.mainCurrency.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Currency.USD
    )

    val selectedValues = MutableStateFlow(NewSubscription())

    val couldSave = selectedValues.map {
        with(it) {
            service != null
                    && period != null
                    && price.toDoubleOrNull() != null
                    && billingDate != null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun saveNewSubscription(currency: Currency) {
        viewModelScope.launch {
            _uiState.update { NewSubscriptionUiState.Loading }
            val values = selectedValues.value
            when {
                values.service == null -> {
                    _uiState.update { NewSubscriptionUiState.Requirement.ServiceRequired }
                    return@launch
                }

                values.period == null -> {
                    _uiState.update { NewSubscriptionUiState.Requirement.PeriodRequired }
                    return@launch
                }

                values.price.toDoubleOrNull() == null -> {
                    _uiState.update { NewSubscriptionUiState.Requirement.PriceRequired }
                    return@launch
                }

                values.billingDate == null -> {
                    _uiState.update { NewSubscriptionUiState.Requirement.BillingDateRequired }
                    return@launch
                }

                values.status == null -> {
                    _uiState.update { NewSubscriptionUiState.Requirement.StatusRequired }
                    return@launch
                }

                else -> {
                    runCatching {
                        val period = values.period

                        val newSubscriptionDb = SubscriptionDb(
                            serviceId = values.service.id,
                            isCustomService = values.service.isCustomService,
                            status = values.status,
                            currency = currency,
                            price = values.price.toDouble(),
                            paymentDate = values.billingDate.toKotlinLocalDateTime(),
                            periodType = period.type,
                            periodDuration = period.duration,
                            description = values.description,
                        )
                        Timber.tag("saveNewSubscription").d(newSubscriptionDb.toString())

                        subscriptionRepository.addSubscription(newSubscriptionDb)
                        Analytics.logAddedSubscription(
                            serviceName = values.service.name,
                            price = values.price,
                            currency = currency,
                            values.service.isCustomService,
                            values.period,
                            values.status,
                        )
                        _uiState.update { NewSubscriptionUiState.Success }
                    }.onFailure { e ->
                        Timber.tag("saveNewSubscription").e(e)
                        _uiState.update { NewSubscriptionUiState.Error }
                        return@launch
                    }

                }

            }
        }
    }

    fun onPriceInput(newPrice: String) {
        if (newPrice.isEmpty() || newPrice.toDoubleOrNull() != null) {
            selectedValues.update { it.copy(price = newPrice) }
        }
    }

    fun onServiceSelected(service: Service) {
        selectedValues.update {
            it.copy(service = service)
        }
    }

    fun onCustomServiceSelected(customService: Service) {
        selectedValues.update {
            it.copy(service = customService)
        }
    }

    fun onResetPeriod() {
        selectedValues.update { it.copy(period = null) }
    }

    fun onResetStatus() {
        selectedValues.update { it.copy(status = null) }
    }


    fun onPeriodSelected(period: BasePeriod) {
        selectedValues.update { it.copy(period = period) }
    }

    fun onBillingDateSelected(date: Long?) {
        selectedValues.update { it.copy(billingDate = date) }
    }

    fun onStatusClicked(newStatus: Status) {
        selectedValues.update { it.copy(status = newStatus) }
    }

    fun onDescriptionChanged(newText: String) {
        selectedValues.update { it.copy(description = newText) }
    }
}

