package com.merkost.suby.model.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.merkost.suby.model.Currency
import com.merkost.suby.model.CustomPeriodType
import com.merkost.suby.model.Period
import com.merkost.suby.model.Status
import com.merkost.suby.utils.now
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "subscription",
)
data class SubscriptionDb(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serviceId: Int,
    val isCustomService: Boolean,
    val price: Double,
    val currency: Currency,

    val period: Period,
    val customPeriodType: CustomPeriodType,
    val customPeriodDuration: Long,

    val status: Status,
    val paymentDate: LocalDateTime,

    val createdDate: LocalDateTime = LocalDateTime.now(),
    val durationDays: Long,

    val description: String,
)
