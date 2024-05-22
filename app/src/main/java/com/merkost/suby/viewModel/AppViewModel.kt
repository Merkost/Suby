package com.merkost.suby.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.Currency
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.room.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    subscriptionRepository: SubscriptionRepository,
    private val appSettings: AppSettings,
) : ViewModel() {

    var isLoading: Boolean = true
        private set

    val isFirstTimeState = MutableStateFlow(true)

    val subscriptions = subscriptionRepository.subscriptionsWithServices
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    val mainCurrency = appSettings.mainCurrency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Currency.USD)

    init {
            appSettings.isFirstTimeLaunch.zip(subscriptions) { isFirstTimeLaunch, _ ->
                isFirstTimeState.update { isFirstTimeLaunch }
                delay(200)
                isLoading = false
            }.launchIn(viewModelScope)
    }

    fun updateFirstTimeOpening() {
        viewModelScope.launch {
            appSettings.saveFirstTimeLaunch(false)
        }
    }

}