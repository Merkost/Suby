package com.merkost.suby.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.Currency
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.room.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    subscriptionRepository: SubscriptionRepository,
    private val appSettings: AppSettings,
) : ViewModel() {

    var isLoading: Boolean = true

    val isFirstTime = MutableStateFlow(false)

    val subscriptions = subscriptionRepository.subscriptions
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val mainCurrency = appSettings.mainCurrency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Currency.USD)

    val total = MutableStateFlow(TotalPrice())

    init {
        viewModelScope.launch {
            appSettings.isFirstTimeLaunch.collectLatest { isF ->
                isFirstTime.update { isF }
                isLoading = false
            }
        }
    }

    fun updateFirstTimeOpening() {
        viewModelScope.launch {
            appSettings.saveFirstTimeLaunch(false)
        }
    }

}