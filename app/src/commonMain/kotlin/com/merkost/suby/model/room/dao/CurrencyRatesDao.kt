package com.merkost.suby.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.room.entity.CurrencyRatesDb
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyRatesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCurrencyRates(currencyRatesDb: CurrencyRatesDb): Long

    @Query("SELECT * FROM currency_rate WHERE mainCurrency = :currency")
    fun getRatesByCurrency(currency: Currency): Flow<CurrencyRatesDb?>

    @Query("SELECT * FROM currency_rate")
    fun getAllRates(): Flow<List<CurrencyRatesDb>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRates(currencyRatesDb: CurrencyRatesDb): Int
}