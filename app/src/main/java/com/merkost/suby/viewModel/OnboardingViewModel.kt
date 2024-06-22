package com.merkost.suby.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.repository.datastore.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val appSettings: AppSettings,
) : ViewModel() {

    fun saveMainCurrency(currency: Currency) {
        viewModelScope.launch {
            appSettings.saveMainCurrency(currency)
        }
    }

}