package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.merkost.suby.R
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.presentation.base.UiState
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.utils.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    val subscriptionId = savedStateHandle.toRoute<Destinations.SubscriptionInfo>().subscriptionId
    val uiState = MutableStateFlow<UiState<Subscription>>(UiState.Loading)

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            subscriptionRepository.removeSubscription(subscription.id)
        }
    }

    fun loadSubscription() {
        viewModelScope.launch {
            val subscription = subscriptionRepository.getSubscriptionById(subscriptionId).first()

            if (subscription == null) {
                uiState.update {
                    UiState.Error(
                        "Subscription not found",
                        R.string.subscription_not_found
                    )
                }
            } else {
                uiState.update { UiState.Success(subscription) }
            }
        }
    }
}
