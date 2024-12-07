package com.merkost.suby.domain


interface CurrencyFormat {
    fun formatIsoCurrencyStyle(
        amount: String,
        currencyCode: String,
    ): String

    fun formatCurrencyStyle(
        amount: String,
        currencyCode: String,
    ): String
}