package com.merkost.suby.repository.datastore

import com.merkost.suby.model.Currency
import kotlinx.datetime.LocalDateTime

data class LastTotalPrice(
    val totalPrice: Double,
    val currency: Currency,
    val lastUpdated: LocalDateTime,
)
