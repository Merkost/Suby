package com.merkost.suby.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.room.entity.NotificationSettingsDb
import com.merkost.suby.repository.datastore.AppSettings
import com.merkost.suby.repository.room.NotificationSettingsRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

class OnboardingViewModel(
    private val appSettings: AppSettings,
    private val notificationSettingsRepository: NotificationSettingsRepository
) : ViewModel() {

    fun saveMainCurrency(currency: Currency) {
        viewModelScope.launch {
            appSettings.saveMainCurrency(currency)
        }
    }

    fun enableNotifications() {
        viewModelScope.launch {
            val defaultSettings = NotificationSettingsDb(
                isEnabled = true,
                daysBeforeReminder = 3,
                enabledStatuses = listOf(Status.ACTIVE),
                notificationTime = LocalTime(9, 0),
                enabledForTrials = true
            )
            notificationSettingsRepository.saveNotificationSettings(defaultSettings)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            appSettings.saveFirstTimeLaunch(false)
        }
    }


}