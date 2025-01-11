package com.merkost.suby.utils

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.datetime.DatePeriod
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.days

object Constants {

    val SUBY_UPDATE_THRESHOLD = 2.days
    const val DEFAULT_CUSTOM_PERIOD_DAYS: Long = 1


    val MAX_FREE_SERVICES = Firebase.remoteConfig.getLong("free_max_subscriptions")
    val MAX_FREE_CUSTOM_SERVICES = Firebase.remoteConfig.getLong("free_max_custom_services")
    val CURRENCY_RATES_CACHE_DAYS = DatePeriod(
        days = Firebase.remoteConfig.getLong("free_currency_rates_update_days").toInt()
    )


    //    todo: Add multiple data formats as a setting
    val dataFormat: DateTimeFormatter
        get() {
            val dateFormatString = "dd/MM/yyyy"
            return DateTimeFormatter.ofPattern(dateFormatString)
        }
}