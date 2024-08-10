package com.merkost.suby.utils

import kotlinx.datetime.DatePeriod
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.days

object Constants {

    val CURRENCY_RATES_CACHE_DAYS = DatePeriod(days = 3)
    val SUBY_UPDATE_THRESHOLD = 2.days
    const val DEFAULT_CUSTOM_PERIOD: Long = 1

//    todo: Add multiple data formats as a setting
    val dataFormat: DateTimeFormatter
        get() {
            val dateFormatString = "dd/MM/yyyy"
            return DateTimeFormatter.ofPattern(dateFormatString)
        }
}