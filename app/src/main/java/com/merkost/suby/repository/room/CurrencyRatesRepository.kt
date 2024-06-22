package com.merkost.suby.repository.room

import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.room.entity.CurrencyRatesDb
import kotlinx.coroutines.flow.Flow

interface CurrencyRatesRepository {
    val rates: Flow<List<CurrencyRatesDb>>
    suspend fun getRatesByMainCurrency(mainCurrency: Currency): Flow<CurrencyRatesDb?>
    suspend fun addCurrencyRates(newCurrencyRatesDb: CurrencyRatesDb)
    suspend fun updateCurrencyRates(currencyRatesDb: CurrencyRatesDb)
}