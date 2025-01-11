package com.merkost.suby.utils

import kotlinx.datetime.DatePeriod
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.days

object Constants {

    // TODO: add firebase remote config
    val CURRENCY_RATES_CACHE_DAYS = DatePeriod(days = 3)
    val SUBY_UPDATE_THRESHOLD = 2.days
    const val DEFAULT_CUSTOM_PERIOD_DAYS: Long = 1
    const val MAX_FREE_SERVICES = 3L
    const val MAX_FREE_CUSTOM_SERVICES = 1L

//    todo: Add multiple data formats as a setting
    val dataFormat: DateTimeFormatter
        get() {
            val dateFormatString = "dd/MM/yyyy"
            return DateTimeFormatter.ofPattern(dateFormatString)
        }
}