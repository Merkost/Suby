package com.merkost.suby.model.entity

import com.merkost.suby.model.Currency
import com.merkost.suby.model.room.entity.CurrencyRates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.serialization.Serializable

@Serializable
data class Rates(
    val date: String,
    val rates: Map<Currency, Double>
) {
    fun toCurrencyRates(mainCurrency: Currency): CurrencyRates {
        return CurrencyRates(
            mainCurrency = mainCurrency,
            rates = rates,
            lastUpdated = LocalDate.parse(date)
                .atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds
        )
    }
}