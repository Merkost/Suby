package com.merkost.suby.model.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ServiceWithCategory(
    @Embedded val service: ServiceDb,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: CategoryDb
)
