package com.merkost.suby.model.room.entity.related

import androidx.room.Embedded
import androidx.room.Relation
import com.merkost.suby.model.room.entity.ServiceDb
import com.merkost.suby.model.room.entity.SubscriptionDb

data class SubscriptionWithDetails(
    @Embedded val subscription: SubscriptionDb,
    @Relation(
        parentColumn = "serviceId",
        entityColumn = "id",
        entity = ServiceDb::class
    )
    val serviceWithCategory: ServiceWithCategory
)