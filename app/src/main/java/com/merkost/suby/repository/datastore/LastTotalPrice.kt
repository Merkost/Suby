package com.merkost.suby.repository.datastore

import com.merkost.suby.model.entity.Currency
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class LastTotalPrice(
    val totalPrice: Double,
    val currency: Currency,
    val lastUpdated: LocalDateTime,
)
