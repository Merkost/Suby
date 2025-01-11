package com.merkost.suby.repository.datastore

import com.merkost.suby.repository.room.SubscriptionRepository
import com.merkost.suby.ui.theme.AppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AppStateRepository(
    ioScope: CoroutineScope,
    appSettings: AppSettings,
    subscriptionRepository: SubscriptionRepository
) {
    val appState = combine(
        appSettings.hasPremium,
        appSettings.isFirstTimeLaunch,
        appSettings.mainCurrency,
        subscriptionRepository.hasAnySubscriptions
    ) { hasPremium, isFirstTimeLaunch, mainCurrency, hasSubscriptions ->
        AppState(
            hasPremium = hasPremium,
            isFirstTimeLaunch = isFirstTimeLaunch,
            mainCurrency = mainCurrency,
            hasSubscriptions = hasSubscriptions
        )
    }.stateIn(ioScope, SharingStarted.Eagerly, AppState())

}