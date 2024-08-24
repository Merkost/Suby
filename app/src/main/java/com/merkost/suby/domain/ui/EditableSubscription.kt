package com.merkost.suby.domain.ui

import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.Status
import com.merkost.suby.model.entity.full.Service
import com.merkost.suby.model.room.entity.PartialSubscriptionDb
import kotlinx.datetime.LocalDateTime

data class EditableSubscription(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val currency: Currency,
    val period: BasePeriod,
    val status: Status,
    val service: Service,
    val billingDate: LocalDateTime,
) {
    fun toSubscriptionDb(): PartialSubscriptionDb {
        return PartialSubscriptionDb(
            id = id,
            description = description,
            price = price,
            currency = currency,
            periodType = period.type,
            periodDuration = period.duration,
            status = status,
            paymentDate = billingDate,
        )
    }
}
