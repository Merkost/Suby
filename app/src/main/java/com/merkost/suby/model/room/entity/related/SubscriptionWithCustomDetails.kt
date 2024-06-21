package com.merkost.suby.model.room.entity.related

import androidx.room.Embedded
import androidx.room.Relation
import com.merkost.suby.model.room.entity.CustomServiceDb
import com.merkost.suby.model.room.entity.SubscriptionDb

data class SubscriptionWithCustomDetails(
    @Embedded val subscription: SubscriptionDb,
    @Relation(
        parentColumn = "serviceId",
        entityColumn = "id",
        entity = CustomServiceDb::class
    )
    val serviceWithCategory: CustomServiceWithCategory
)