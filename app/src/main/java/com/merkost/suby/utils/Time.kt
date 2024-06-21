package com.merkost.suby.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime

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
