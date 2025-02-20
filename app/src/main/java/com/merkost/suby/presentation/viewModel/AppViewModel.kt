package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.billing.BillingService
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.datastore.AppStateRepository
import com.merkost.suby.repository.room.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber


class AppViewModel(
    private val billingService: BillingService,
    private val appSettings: AppSettings,
    private val appStateRepository: AppStateRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    val isAppReady = MutableStateFlow(false)

    init {
        preloadAppData()
    }

    private fun preloadAppData() {
        viewModelScope.launch {
            appStateRepository.appState.firstOrNull()

            val entitlements = billingService.getEntitlements()
            if (entitlements.isEmpty()) {
                appSettings.saveHasPremium(false)
            } else {
                Timber.tag("AppViewModel").w("Entitlements: $entitlements")
                appSettings.saveHasPremium(entitlements.any { it.isActive })
            }
            subscriptionRepository.subscriptions
            isAppReady.value = true
        }
    }

    fun updateFirstTimeOpening() {
        viewModelScope.launch {
            appSettings.saveFirstTimeLaunch(false)
        }
    }

}