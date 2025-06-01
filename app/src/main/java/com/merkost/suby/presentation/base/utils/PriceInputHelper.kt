package com.merkost.suby.presentation.base.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class PriceInputConfig(
    val decimalSeparator: Char,
    val groupingSeparator: Char,
    val placeholder: String,
    val maxDecimalPlaces: Int = 2
)

@Composable
fun rememberPriceInputConfig(locale: Locale = Locale.getDefault()): PriceInputConfig {
    return remember(locale) {
        val symbols = DecimalFormatSymbols.getInstance(locale)
        val decimalFormat = DecimalFormat.getCurrencyInstance(locale) as DecimalFormat
        
        val placeholder = buildString {
            append("0")
            append(symbols.decimalSeparator)
            repeat(2) { append("0") }
        }
        
        PriceInputConfig(
            decimalSeparator = symbols.decimalSeparator,
            groupingSeparator = symbols.groupingSeparator,
            placeholder = placeholder,
            maxDecimalPlaces = decimalFormat.maximumFractionDigits
        )
    }
}

object PriceInputValidator {
    
    fun validatePriceInput(
        input: String,
        config: PriceInputConfig,
        maxValue: Double = Double.MAX_VALUE
    ): String {
        if (input.isEmpty()) return input
        
        val cleanedInput = input.replace(config.groupingSeparator.toString(), "")
        
        if (cleanedInput == config.decimalSeparator.toString()) {
            return "0${config.decimalSeparator}"
        }
        
        val decimalParts = cleanedInput.split(config.decimalSeparator)
        
        if (decimalParts.size > 2) {
            return input.dropLast(1)
        }
        
        val integerPart = decimalParts[0]
        if (integerPart.any { !it.isDigit() }) {
            return input.dropLast(1)
        }
        
        if (decimalParts.size == 2) {
            val decimalPart = decimalParts[1]
            if (decimalPart.any { !it.isDigit() }) {
                return input.dropLast(1)
            }
            if (decimalPart.length > config.maxDecimalPlaces) {
                return input.dropLast(1)
            }
        }
        
        val value = parsePrice(cleanedInput, config)
        if (value != null && value > maxValue) {
            return input.dropLast(1)
        }
        
        return input
    }
    
    fun parsePrice(input: String, config: PriceInputConfig): Double? {
        if (input.isEmpty() || input == config.decimalSeparator.toString()) return 0.0
        
        val normalized = input.replace(config.decimalSeparator, '.')
        return normalized.toDoubleOrNull()
    }
    
    fun formatPriceForDisplay(
        value: Double?,
        config: PriceInputConfig,
        showTrailingZeros: Boolean = false
    ): String {
        if (value == null || value == 0.0) return ""
        
        val format = if (showTrailingZeros) {
            DecimalFormat().apply {
                minimumFractionDigits = config.maxDecimalPlaces
                maximumFractionDigits = config.maxDecimalPlaces
                decimalFormatSymbols = DecimalFormatSymbols().apply {
                    decimalSeparator = config.decimalSeparator
                    groupingSeparator = config.groupingSeparator
                }
            }
        } else {
            DecimalFormat().apply {
                minimumFractionDigits = 0
                maximumFractionDigits = config.maxDecimalPlaces
                decimalFormatSymbols = DecimalFormatSymbols().apply {
                    decimalSeparator = config.decimalSeparator
                    groupingSeparator = config.groupingSeparator
                }
            }
        }
        
        return format.format(value)
    }
    
    fun getPlaceholderForCurrency(
        currencyCode: String,
        config: PriceInputConfig,
        locale: Locale = Locale.getDefault()
    ): String {
        return try {
            val format = DecimalFormat.getCurrencyInstance(locale) as DecimalFormat
            format.decimalFormatSymbols = format.decimalFormatSymbols.apply {
                decimalSeparator = config.decimalSeparator
            }
            
            val sampleValue = 0.0
            format.format(sampleValue).replace(Regex("[^\\d${config.decimalSeparator}]"), "")
                .ifEmpty { config.placeholder }
        } catch (e: Exception) {
            config.placeholder
        }
    }
} 