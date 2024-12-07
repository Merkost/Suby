package com.merkost.suby.domain

import android.icu.text.NumberFormat
import android.icu.util.Currency
import timber.log.Timber
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

data class CurrencyFormatException(val amount: String, val currencyCode: String) :
    Throwable("Formatting Currency Failed: amount: $amount currencyCode: $currencyCode") {
    companion object {
        fun createException(amount: String, currencyCode: String) =
            CurrencyFormatException(amount, currencyCode)
    }
}

class CurrencyFormatImpl @Inject constructor(
    private val locale: Locale
) : CurrencyFormat {
    @Deprecated("Use the one with bigDecimal")
    override fun formatIsoCurrencyStyle(amount: String, currencyCode: String): String =
        formatCurrent(
            amount,
            currencyCode,
            NumberFormat.ISOCURRENCYSTYLE
        )


    @Deprecated("Use the one with bigDecimal")
    override fun formatCurrencyStyle(amount: String, currencyCode: String): String = formatCurrent(
        amount,
        currencyCode,
        NumberFormat.CURRENCYSTYLE
    )


    private fun formatCurrent(amount: String, currencyCode: String, style: Int): String {
        return kotlin.runCatching {
            val value = amount.toDouble()

            val numberFormat = currencyInLocale(
                style,
                currencyCode,
            )
            numberFormat.format(value)
        }.onFailure {
            Timber.tag("CurrencyFormat").w(
                CurrencyFormatException.createException(
                    amount,
                    currencyCode
                )
            )
        }.getOrDefault("")
    }

    private fun formatCurrent(amount: BigDecimal, currencyCode: String, style: Int): String {
        return kotlin.runCatching {
            val value = amount.toDouble()

            val numberFormat = currencyInLocale(
                style,
                currencyCode,
            )
            numberFormat.format(value)
        }.onFailure {
            Timber.tag("CurrencyFormat").w(
                CurrencyFormatException.createException(
                    amount.toPlainString(),
                    currencyCode
                )
            )
        }.getOrDefault("")
    }

    private fun currencyInLocale(
        style: Int,
        currencyCode: String,
    ): NumberFormat = Locale(
        locale.language,
        locale.country
    ).let {
        NumberFormat.getInstance(it, style).apply {
            currency = Currency.getInstance(currencyCode)
        }
    }
}