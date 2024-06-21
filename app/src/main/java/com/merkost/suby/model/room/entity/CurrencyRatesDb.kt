package com.merkost.suby.model.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.merkost.suby.model.Currency
import kotlinx.datetime.LocalDate

@Entity(tableName = "currency_rate")
data class CurrencyRatesDb(
    @PrimaryKey(autoGenerate = false)
    val mainCurrency: Currency,
    val rates: Map<Currency, Double>,
    val lastUpdated: LocalDate,
)
