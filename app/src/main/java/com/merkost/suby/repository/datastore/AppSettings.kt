package com.merkost.suby.repository.datastore

import com.merkost.suby.model.entity.Currency
import kotlinx.coroutines.flow.Flow


interface AppSettings {

    val hasPremium: Flow<Boolean>
    suspend fun saveHasPremium(newValue: Boolean)

    val isFirstTimeLaunch: Flow<Boolean>
    suspend fun saveFirstTimeLaunch(newValue: Boolean)

    val mainCurrency: Flow<Currency>
    suspend fun saveMainCurrency(currency: Currency)

    val lastTotalPrice: Flow<LastTotalPrice?>
    suspend fun saveLastTotalPrice(lastTotalPrice: LastTotalPrice)

}