package com.merkost.suby.presentation.base.utils

import androidx.compose.runtime.Composable
import java.util.Locale

@Composable
fun Double.toLocalizedPriceString(
    locale: Locale = Locale.getDefault(),
    showTrailingZeros: Boolean = false
): String {
    val config = rememberPriceInputConfig(locale)
    return PriceInputValidator.formatPriceForDisplay(
        value = this,
        config = config,
        showTrailingZeros = showTrailingZeros
    )
}

@Composable
fun String.parseLocalizedPrice(
    locale: Locale = Locale.getDefault()
): Double? {
    val config = rememberPriceInputConfig(locale)
    return PriceInputValidator.parsePrice(this, config)
}

@Composable
fun String.isValidPriceInput(
    locale: Locale = Locale.getDefault(),
    maxValue: Double = Double.MAX_VALUE
): Boolean {
    val config = rememberPriceInputConfig(locale)
    val validated = PriceInputValidator.validatePriceInput(this, config, maxValue)
    return validated == this
}

fun Locale.getDecimalSeparator(): Char {
    return java.text.DecimalFormatSymbols.getInstance(this).decimalSeparator
}

fun Locale.getGroupingSeparator(): Char {
    return java.text.DecimalFormatSymbols.getInstance(this).groupingSeparator
} 