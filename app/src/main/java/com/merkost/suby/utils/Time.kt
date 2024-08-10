package com.merkost.suby.utils

import android.icu.text.DateFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

fun LocalDateTime.toEpochMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.toInstant(timeZone).toEpochMilliseconds()
}

fun Long.toKotlinLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

fun LocalDateTime.Companion.now(): LocalDateTime {
    return java.time.LocalDateTime.now().toKotlinLocalDateTime()
}

val currentMoment: Instant = Clock.System.now()

fun LocalDate.Companion.now() =
    currentMoment.toLocalDateTime(TimeZone.currentSystemDefault()).date


val Long.toLocalDate: LocalDate
    get() = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date

fun Long.dateString(dateFormat: Int = DateFormat.MEDIUM): String {
    val format: DateFormat = DateFormat.getDateInstance(dateFormat, Locale.getDefault())
    val date = Date(this)
    return format.format(date)
}

fun java.time.LocalDate.dateString(dateFormat: Int = DateFormat.MEDIUM): String {
    val epochMilli = this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return epochMilli.dateString(dateFormat)
}

fun LocalDateTime.toRelativeTimeString(): String {
    val now = LocalDateTime.now().toJavaLocalDateTime()
    val minutesBetween = ChronoUnit.MINUTES.between(this.toJavaLocalDateTime(), now)
    val daysBetween = ChronoUnit.DAYS.between(this.toJavaLocalDateTime(), now)

    return when {
        minutesBetween < 5 -> "Just now"
        daysBetween < 1 -> "Today"
        daysBetween == 1L -> "Yesterday"
        daysBetween < 7 -> "$daysBetween days ago"
        else -> {
            val weeksBetween = ChronoUnit.WEEKS.between(this.toJavaLocalDateTime(), now)
            if (weeksBetween == 1L) "Last week" else "$weeksBetween weeks ago"
        }
    }
}
