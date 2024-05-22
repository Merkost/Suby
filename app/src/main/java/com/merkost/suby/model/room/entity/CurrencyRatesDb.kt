package com.merkost.suby.model.room.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.merkost.suby.model.Currency
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "currency_rates")
data class CurrencyRatesDb(
    @PrimaryKey(autoGenerate = false)
    val mainCurrency: Currency,
    val rates: Map<Currency, Double>,
    val lastUpdated: Long = System.currentTimeMillis(),
) : Parcelable
