package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.room.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    subscriptionRepository: SubscriptionRepository,
    private val appSettings: AppSettings,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val isFirstTimeLaunch = appSettings.isFirstTimeLaunch

    val subscriptions = subscriptionRepository.subscriptions
    val hasSubscriptions = subscriptions.map { it.isNotEmpty() }

    val mainCurrency = appSettings.mainCurrency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Currency.USD)

    fun updateFirstTimeOpening() {
        viewModelScope.launch {
            appSettings.saveFirstTimeLaunch(false)
        }
    }
}