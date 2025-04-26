package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.merkost.suby.R
import com.merkost.suby.model.entity.full.Subscription
import com.merkost.suby.presentation.base.BaseUiState
import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.utils.Destinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import suby.app.generated.resources.Res
import suby.app.generated.resources.subscription_not_found


class SubscriptionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    val subscriptionId = savedStateHandle.toRoute<Destinations.SubscriptionInfo>().subscriptionId
    val uiState = MutableStateFlow<BaseUiState<Subscription>>(BaseUiState.Loading)

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
                        Res.string.subscription_not_found
                    )
                }
            } else {
                uiState.update { BaseUiState.Success(subscription) }
            }
        }
    }
}
