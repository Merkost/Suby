package com.merkost.suby.repository.room

import com.merkost.suby.model.Currency
import com.merkost.suby.model.room.entity.CurrencyRates
import kotlinx.coroutines.flow.Flow

interface CurrencyRatesRepository {
    val rates: Flow<List<CurrencyRates>>
    suspend fun getRatesByMainCurrency(mainCurrency: Currency): Flow<CurrencyRates?>
    suspend fun addCurrencyRates(newCurrencyRates: CurrencyRates)
    suspend fun updateCurrencyRates(currencyRates: CurrencyRates)
}