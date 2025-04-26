package com.merkost.suby.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until

interface PlatformDateTimeFormat {
    fun format(dateTime: LocalDateTime): String
}

interface PlatformDateFormat {
    fun format(date: LocalDate): String
}

internal expect fun localizedDateTimeFormat(pattern: String): PlatformDateTimeFormat
internal expect fun localizedDateFormat(pattern: String): PlatformDateFormat

fun LocalDateTime.format(formatter: PlatformDateTimeFormat): String = formatter.format(this)

fun LocalDate.format(formatter: PlatformDateFormat): String = formatter.format(this)

object DateFormats {
    val JUST_DATE: PlatformDateFormat = localizedDateFormat("dd MMM yyyy")
    val SHORT_DATE: PlatformDateFormat = localizedDateFormat("dd MM yyyy")
    val FULL_DATE_WITH_WEEKDAY: PlatformDateFormat = localizedDateFormat("EEEE, dd MMM yyyy")
    val DATE_WITHOUT_TIME: PlatformDateFormat = localizedDateFormat("EEE, dd MMM yyyy")
}

object TimeFormats {
    val HOUR_MINUTE_AM_PM: PlatformDateTimeFormat = localizedDateTimeFormat("h:mm a")
    val HOUR_MINUTE_24H: PlatformDateTimeFormat = localizedDateTimeFormat("HH:mm")
    val FULL_TIME: PlatformDateTimeFormat = localizedDateTimeFormat("HH:mm:ss")
}

object DateTimeFormats {
    val FULL_DATE_TIME: PlatformDateTimeFormat = localizedDateTimeFormat("dd MMM yyyy HH:mm")
    val SHORT_DATETIME: PlatformDateTimeFormat = localizedDateTimeFormat("dd/MM/yyyy HH:mm")
    val TRANSACTION_DATETIME: PlatformDateTimeFormat = localizedDateTimeFormat("dd MMM yyyy, h:mm a")
}

fun LocalDateTime.toEpochMillis(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.toInstant(timeZone).toEpochMilliseconds()
}

fun Long.toKotlinLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

fun LocalDateTime.Companion.now(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

val currentMoment: Instant = Clock.System.now()

fun LocalDate.Companion.now() =
    currentMoment.toLocalDateTime(TimeZone.currentSystemDefault()).date


val Long.toLocalDate: LocalDate
    get() = Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date


fun LocalDateTime.toRelativeTimeString(
    zone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val nowInstant = Clock.System.now()
    val thisInstant = this.toInstant(zone)

    val minutesBetween = thisInstant.until(nowInstant, DateTimeUnit.MINUTE)
    val daysBetween = thisInstant.daysUntil(nowInstant, zone)

    return when {
        minutesBetween < 5 -> "Just now"
        daysBetween < 1 -> "Today"
        daysBetween == 1 -> "Yesterday"
        daysBetween < 7 -> "$daysBetween days ago"
        else -> {
            val weeksBetween = thisInstant.until(nowInstant, DateTimeUnit.WEEK, zone)
            if (weeksBetween == 1L) "Last week" else "$weeksBetween weeks ago"
        }
    }
}

fun LocalDate.formatDate(): String {
    val month = month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "$month $dayOfMonth, $year"
}

fun Long.dateString(): String =
    Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .formatDate()
