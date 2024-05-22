package com.merkost.suby.repository.room

import com.merkost.suby.model.Currency
import com.merkost.suby.model.room.dao.CurrencyRatesDao
import com.merkost.suby.model.room.entity.CurrencyRatesDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CurrencyRatesRepositoryImpl(private val currencyRatesDao: CurrencyRatesDao) :
    CurrencyRatesRepository {

    override val rates: Flow<List<CurrencyRatesDb>> = currencyRatesDao.getAllRates()

    override suspend fun getRatesByMainCurrency(mainCurrency: Currency) =
        currencyRatesDao.getRatesByCurrency(mainCurrency)

    override suspend fun addCurrencyRates(newCurrencyRatesDb: CurrencyRatesDb) {
        withContext(Dispatchers.IO) {
            currencyRatesDao.addCurrencyRates(newCurrencyRatesDb)
        }
    }

    override suspend fun updateCurrencyRates(currencyRatesDb: CurrencyRatesDb) {
        withContext(Dispatchers.IO) {
            currencyRatesDao.updateRates(currencyRatesDb)
        }
    }

}