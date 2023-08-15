package com.merkost.suby.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.Category
import com.merkost.suby.model.Currency
import com.merkost.suby.model.Period
import com.merkost.suby.model.Service
import com.merkost.suby.model.room.entity.Subscription
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.room.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewSubscriptionViewModel @Inject constructor(
    appSettings: AppSettings,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState?>(null)
    val uiState = _uiState.asStateFlow()

    val mainCurrency = appSettings.mainCurrency.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Currency.USD
    )

    val selectedValues = MutableStateFlow(NewSubscription())

    fun saveNewSubscription(currency: Currency) {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }
            with(selectedValues.value) {

                runCatching {
                    val newSubscription = Subscription(
                        service = service!!,
                        category = category!!,
                        durationDays = period!!.days,
                        currency = currency,
                        price = price.toDouble(),
                        paymentDate = billingDate!!
                    )
                    subscriptionRepository.addSubscription(newSubscription)
                    _uiState.update { UiState.Success }
                }.onFailure {
                    _uiState.update { UiState.Error }
                    return@launch
                }
            }
        }
    }

    fun onPriceInput(newPrice: String) {
        if (newPrice.isEmpty() || newPrice.toDoubleOrNull() != null) {
            selectedValues.update { it.copy(price = newPrice) }
        }
    }

    fun onServiceSelected(category: Category, service: Service) {
        selectedValues.update {
            it.copy(
                service = service,
                category = category
            )
        }
    }

    fun onResetPeriod() {
        viewModelScope.launch {
            selectedValues.update { it.copy(period = null) }
        }
    }

    fun onPeriodSelected(period: Period) {
        viewModelScope.launch {
            selectedValues.update { it.copy(period = period) }
        }
    }

    fun onBillingDateSelected(date: Long) {
        viewModelScope.launch {
            selectedValues.update { it.copy(billingDate = date) }
        }
    }
}

data class NewSubscription(
    val price: String = "",
    val category: Category? = null,
    val customCategoryName: String = "",
    val service: Service? = null,
    val customServiceName: String = "",
    val period: Period? = null,
    val billingDate: Long? = null,
)
