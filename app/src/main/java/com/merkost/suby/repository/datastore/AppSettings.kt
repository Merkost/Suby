package com.merkost.suby.repository.datastore

import com.merkost.suby.model.Currency
import kotlinx.coroutines.flow.Flow


interface AppSettings {

    val isFirstTimeLaunch: Flow<Boolean>
    suspend fun saveFirstTimeLaunch(newValue: Boolean)

    val mainCurrency: Flow<Currency>
    suspend fun saveMainCurrency(currency: Currency)

    val lastTotalPrice: Flow<LastTotalPrice?>
    suspend fun saveLastTotalPrice(lastTotalPrice: LastTotalPrice)

}