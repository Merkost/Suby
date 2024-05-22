package com.merkost.suby.utils

import java.time.format.DateTimeFormatter

object Constants {

    const val DEFAULT_CUSTOM_PERIOD: Long = 1

    val dataFormat: DateTimeFormatter
        get() {
            val dateFormatString = "dd/MM/yyyy"
            return DateTimeFormatter.ofPattern(dateFormatString)
        }
}