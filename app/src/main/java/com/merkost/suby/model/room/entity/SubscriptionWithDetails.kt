package com.merkost.suby.model.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SubscriptionWithDetails(
    @Embedded val subscription: SubscriptionDb,

    @Relation(
        entity = ServiceDb::class,
        parentColumn = "serviceId",
        entityColumn = "serviceId"
    )
    val serviceWithCategory: ServiceWithCategory
)