package com.merkost.suby.model.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.CustomPeriod
import com.merkost.suby.model.entity.Status
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

    val periodType: CustomPeriod,
    val periodDuration: Long,

    val status: Status,
    val paymentDate: LocalDateTime,

    val createdDate: LocalDateTime = LocalDateTime.now(),
    val description: String,
)
