package com.merkost.suby.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

actual fun localizedDateTimeFormat(pattern: String): PlatformDateTimeFormat =
    object : PlatformDateTimeFormat {
        private val formatter = DateTimeFormatter.ofPattern(pattern)

        override fun format(dateTime: LocalDateTime): String {
            val javaDateTime = dateTime.toJavaLocalDateTime()
            return formatter.format(javaDateTime)
        }
    }

actual fun localizedDateFormat(pattern: String): PlatformDateFormat =
    object : PlatformDateFormat {
        private val formatter = DateTimeFormatter.ofPattern(pattern)

        override fun format(date: LocalDate): String {
            val javaDate = date.toJavaLocalDate()
            return formatter.format(javaDate)
        }
    }