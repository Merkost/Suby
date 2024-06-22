package com.merkost.suby.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.utils.BaseViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val subscriptionRepository: SubscriptionRepository,
    private val appSettings: AppSettings,
) : ViewModel() {

    val uiState = MutableStateFlow<BaseViewState<Subscription>>(BaseViewState.Loading)

    val subscriptions = subscriptionRepository.subscriptions
        .stateIn(viewModelScope, SharingStarted.Lazily, listOf())

    val mainCurrency = appSettings.mainCurrency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Currency.USD)

    fun updateMainCurrency(currency: Currency) {
        viewModelScope.launch {
            appSettings.saveMainCurrency(currency)
        }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            subscriptionRepository.removeSubscription(subscription.id)
        }
    }

    fun loadSubscription(subscriptionId: Int) {
        viewModelScope.launch {
            val subscription = subscriptionRepository.getSubscriptionById(subscriptionId).first()

            if (subscription == null) {
                uiState.update { BaseViewState.Error() }
            } else {
                uiState.update { BaseViewState.Success(subscription) }
            }
        }
    }
}
