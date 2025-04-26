package com.merkost.suby.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.merkost.suby.shared.EnvironmentManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.get
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.datetime.DatePeriod
import kotlin.time.Duration.Companion.days

object AndroidConstants {
    const val CURRENCY_ENDPOINT_FREE =
        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies"
    const val CURRENCY_ENDPOINT_FALLBACK = "https://latest.currency-api.pages.dev/v1/currencies"

    val SUPABASE_ENDPOINT = "https://${EnvironmentManager.supabaseId}.supabase.co"

    val SubyShape = RoundedCornerShape(25)
    val SubySmallShape = RoundedCornerShape(10)


    val LAZY_PADDING = 64.dp
    val SUBY_UPDATE_THRESHOLD = 2.days
    const val DEFAULT_CUSTOM_PERIOD_DAYS: Long = 1

    val MAX_FREE_SERVICES = Firebase.remoteConfig.get<Long>("free_max_subscriptions")
    val MAX_FREE_CUSTOM_SERVICES = Firebase.remoteConfig.get<Long>("free_max_custom_services")
    val CURRENCY_RATES_CACHE_DAYS = DatePeriod(
        days = Firebase.remoteConfig.get<Long>("free_currency_rates_update_days").toInt()
    )
}