package com.merkost.suby.presentation.base

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.presentation.base.utils.PriceInputValidator
import com.merkost.suby.presentation.base.utils.rememberPriceInputConfig
import java.util.Locale

@Composable
fun ImprovedPriceField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    currency: Currency,
    textStyle: TextStyle = LocalTextStyle.current,
    locale: Locale = Locale.getDefault(),
    maxValue: Double = Double.MAX_VALUE,
    showTrailingZeros: Boolean = false
) {
    val config = rememberPriceInputConfig(locale)
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val placeholder = remember(currency, config) {
        PriceInputValidator.getPlaceholderForCurrency(
            currencyCode = currency.code,
            config = config,
            locale = locale
        )
    }

    val displayValue = remember(value, isFocused, showTrailingZeros) {
        if (isFocused || value.isEmpty()) {
            value
        } else {
            val parsedPrice = PriceInputValidator.parsePrice(value, config)
            PriceInputValidator.formatPriceForDisplay(
                value = parsedPrice,
                config = config,
                showTrailingZeros = showTrailingZeros
            )
        }
    }

    SubyTextField(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        value = displayValue,
        onValueChange = { newValue ->
            val validatedValue = PriceInputValidator.validatePriceInput(
                input = newValue,
                config = config,
                maxValue = maxValue
            )
            onValueChange(validatedValue)
        },
        textStyle = textStyle,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        placeholder = {
            Text(
                text = placeholder,
                style = textStyle.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        },
        prefix = {
            Text(
                text = currency.symbol,
                style = textStyle,
                overflow = TextOverflow.Visible,
                maxLines = 1
            )
        }
    )
} 