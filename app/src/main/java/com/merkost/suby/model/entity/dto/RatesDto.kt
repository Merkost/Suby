package com.merkost.suby.model.entity.dto

import com.merkost.suby.model.Currency
import com.merkost.suby.model.room.entity.CurrencyRatesDb
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.serialization.Serializable

@Serializable
data class RatesDto(
    val date: String,
    val rates: Map<Currency, Double>
) {
    fun toCurrencyRates(mainCurrency: Currency): CurrencyRatesDb {
        return CurrencyRatesDb(
            mainCurrency = mainCurrency,
            rates = rates,
            lastUpdated = LocalDate.parse(date)
                .atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds
        )
    }
}