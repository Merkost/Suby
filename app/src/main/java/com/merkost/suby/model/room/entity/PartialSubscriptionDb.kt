package com.merkost.suby.model.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.CustomPeriod
import com.merkost.suby.model.entity.Status
import kotlinx.datetime.LocalDateTime

@Entity
data class PartialSubscriptionDb(
    @PrimaryKey
    val id: Int,
    val price: Double,
    val currency: Currency,
    val periodType: CustomPeriod,
    val periodDuration: Long,
    val status: Status,
    val isTrial: Boolean = false,
    val description: String,
    val paymentDate: LocalDateTime,
    val paymentStartDate: LocalDateTime? = null,
)
