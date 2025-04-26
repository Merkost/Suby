package com.merkost.suby.domain

import java.math.BigDecimal


interface CurrencyFormat {
    fun formatIsoCurrencyStyle(
        amount: BigDecimal,
        currencyCode: String,
    ): String

    fun formatCurrencyStyle(
        amount: BigDecimal,
        currencyCode: String,
    ): String
}