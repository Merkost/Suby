package com.merkost.suby.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual fun localizedDateTimeFormat(pattern: String): PlatformDateTimeFormat =
    object : PlatformDateTimeFormat {
        private val formatter = NSDateFormatter().apply {
            dateFormat = pattern
            locale = NSLocale.currentLocale
        }

        override fun format(dateTime: LocalDateTime): String {
            val nsDate = dateTime.toNSDate(TimeZone.currentSystemDefault())
            return formatter.stringFromDate(nsDate)
        }
    }

actual fun localizedDateFormat(pattern: String): PlatformDateFormat =
    object : PlatformDateFormat {
        private val formatter = NSDateFormatter().apply {
            dateFormat = pattern
            locale = NSLocale.currentLocale
        }

        override fun format(date: LocalDate): String {
            val nsDate = date.toNSDateAtStartOfDayIn(TimeZone.currentSystemDefault())
            return formatter.stringFromDate(nsDate)
        }
    }

fun LocalDate.toNSDateAtStartOfDayIn(timeZone: TimeZone) =
    atStartOfDayIn(timeZone).toNSDate()

fun LocalDateTime.toNSDate(timeZone: TimeZone) =
    toInstant(timeZone).toNSDate()