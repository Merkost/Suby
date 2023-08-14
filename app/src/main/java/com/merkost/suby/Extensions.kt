package com.merkost.suby

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.Dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val Dp.asWindowInsets: WindowInsets
    get() = WindowInsets(this, this, this, this)

fun Long.formatDateLongToDate(): String {
    val date = Date(this)
    val sdf = SimpleDateFormat("dd MMMM", Locale.getDefault())
    return sdf.format(date)
}

fun Double.formatDecimal(): String {
    return if (this == this.toLong().toDouble()) {
        this.toLong().toString()
    } else {
        String.format("%.2f", this)
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}


val currentMoment: Instant = Clock.System.now()

fun LocalDate.Companion.now() =
    currentMoment.toLocalDateTime(TimeZone.currentSystemDefault()).date


val Long.toLocalDate: LocalDate
    get() = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date


fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}