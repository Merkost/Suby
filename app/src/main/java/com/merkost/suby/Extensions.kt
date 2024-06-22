package com.merkost.suby

import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.Dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

val Dp.asWindowInsets: WindowInsets
    get() = WindowInsets(this, this, this, this)

fun Double.formatDecimal(): String {
    val formatter = DecimalFormat("#.##").apply {
        isDecimalSeparatorAlwaysShown = false
    }
    return formatter.format(this)}

fun String?.toSafeLocalDateTime(): LocalDateTime? =
    runCatching { this?.toLocalDateTime() }.getOrNull()

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(textId: Int) {
    Toast.makeText(this, textId, Toast.LENGTH_SHORT).show()
}

fun Double.round(): String {
    val roundedValue = (this * 100.0).toInt() / 100.0
    return roundedValue.toString()
}