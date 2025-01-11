package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.billing.BillingService
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.datastore.AppStateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber


class AppViewModel(
    private val billingService: BillingService,
    private val appSettings: AppSettings,
    private val appStateRepository: AppStateRepository
) : ViewModel() {

    val isReadyForLaunch = MutableStateFlow(false)

    init {
        getReadyForAppLaunch()
    }

    private fun getReadyForAppLaunch() {
        viewModelScope.launch {
            appStateRepository.appState.firstOrNull()
            val entitlements = billingService.getEntitlements()
            if (entitlements.isEmpty()) {
                appSettings.saveHasPremium(false)
            } else {
                Timber.tag("AppViewModel").w("Entitlements: $entitlements")
                appSettings.saveHasPremium(entitlements.any { it.isActive })
            }
            isReadyForLaunch.update { true }
        }
    }

    fun updateFirstTimeOpening() {
        viewModelScope.launch {
            appSettings.saveFirstTimeLaunch(false)
        }
    }
}