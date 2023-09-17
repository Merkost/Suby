package com.merkost.suby.repository.room

import com.merkost.suby.model.Currency
import com.merkost.suby.model.room.dao.CurrencyRatesDao
import com.merkost.suby.model.room.entity.CurrencyRates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CurrencyRatesRepositoryImpl(private val currencyRatesDao: CurrencyRatesDao) :
    CurrencyRatesRepository {

    override val rates: Flow<List<CurrencyRates>> = currencyRatesDao.getAllRates()

    override suspend fun getRatesByMainCurrency(mainCurrency: Currency) =
        currencyRatesDao.getRatesByCurrency(mainCurrency)

    override suspend fun addCurrencyRates(newCurrencyRates: CurrencyRates) {
        withContext(Dispatchers.IO) {
            currencyRatesDao.addCurrencyRates(newCurrencyRates)
        }
    }

    override suspend fun updateCurrencyRates(currencyRates: CurrencyRates) {
        withContext(Dispatchers.IO) {
            currencyRatesDao.updateRates(currencyRates)
        }
    }

}