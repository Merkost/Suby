package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.R
import com.merkost.suby.domain.ui.EditableSubscription
import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.presentation.base.BaseUiState
import com.merkost.suby.presentation.states.EditSubscriptionEvent
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.utils.toKotlinLocalDateTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class EditSubscriptionViewModel(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _uiEvents = MutableSharedFlow<EditSubscriptionEvent?>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val _uiState = MutableStateFlow<BaseUiState<Unit>>(BaseUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val subscription = MutableStateFlow<Subscription?>(null)

    private val _subscriptionEdit = MutableStateFlow<EditableSubscription?>(null)
    val subscriptionEdit: StateFlow<EditableSubscription?> = _subscriptionEdit

    val couldSave = _subscriptionEdit.map {
        subscription.value?.toEditableSubscription() != it
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun loadSubscription(subscriptionId: Int, pickedCurrency: Currency?) {
        val currentSubscription = subscriptionEdit.value
        if (currentSubscription != null && currentSubscription.id == subscriptionId) {
            _subscriptionEdit.update {
                currentSubscription.copy(
                    currency = pickedCurrency ?: currentSubscription.currency
                )
            }
            return
        }

        viewModelScope.launch {
            val subscription = repository.getSubscriptionById(subscriptionId).firstOrNull()
            if (subscription == null) {
                _uiState.update {
                    BaseUiState.Error(
                        "Subscription not found",
                        R.string.subscription_not_found
                    )
                }
            } else {
                val editableSubscription = subscription.toEditableSubscription()
                _subscriptionEdit.update { editableSubscription }
                _uiState.update { BaseUiState.Success(Unit) }
            }
        }
    }

    private fun updateSubscriptionState(update: (EditableSubscription) -> EditableSubscription) {
        val currentSubscription = _subscriptionEdit.value
        currentSubscription?.let {
            _subscriptionEdit.update { update(currentSubscription) }
        }
    }

    fun onDescriptionChanged(description: String) {
        updateSubscriptionState { it.copy(description = description) }
    }

    fun onPriceChanged(price: String) {
        price.toDoubleOrNull()?.let { priceValue ->
            updateSubscriptionState { it.copy(price = priceValue) }
        }
    }

    fun onBillingDateChanged(billingDate: Long?) {
        billingDate?.let { dateLong ->
            updateSubscriptionState { it.copy(billingDate = dateLong.toKotlinLocalDateTime()) }
        }
    }

    fun onServiceChanged(service: Service) {
        updateSubscriptionState { it.copy(service = service) }
    }

    fun onStatusChanged(status: Status) {
        updateSubscriptionState { it.copy(status = status) }
    }

    fun onTrialChanged(isTrial: Boolean) {
        updateSubscriptionState { it.copy(isTrial = isTrial) }
    }

    fun onPeriodChanged(period: BasePeriod) {
        updateSubscriptionState { it.copy(period = period) }
    }

    fun saveSubscription() {
        val currentSubscription = _subscriptionEdit.value
        currentSubscription?.let { subscription ->
            viewModelScope.launch {
                repository.updateSubscription(subscription.toSubscriptionDb())
                _uiEvents.emit(EditSubscriptionEvent.SubscriptionSaved)
            }
        }
    }
}