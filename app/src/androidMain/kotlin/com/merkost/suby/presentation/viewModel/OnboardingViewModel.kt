package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.repository.datastore.AppSettings
import kotlinx.coroutines.launch


class OnboardingViewModel(
    private val appSettings: AppSettings,
) : ViewModel() {

    fun saveMainCurrency(currency: Currency) {
        viewModelScope.launch {
            appSettings.saveMainCurrency(currency)
        }
    }

}