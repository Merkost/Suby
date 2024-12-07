package com.merkost.suby.domain.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.merkost.suby.domain.CurrencyFormat
import com.merkost.suby.domain.CurrencyFormatImpl
import java.util.Locale

val LocalCurrencyFormatter = staticCompositionLocalOf<CurrencyFormat> {
    CurrencyFormatImpl(Locale.getDefault())
}