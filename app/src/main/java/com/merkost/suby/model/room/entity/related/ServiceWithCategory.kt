package com.merkost.suby.model.room.entity.related

import androidx.room.Embedded
import androidx.room.Relation
import com.merkost.suby.model.room.entity.CategoryDb
import com.merkost.suby.model.room.entity.ServiceDb

data class ServiceWithCategory(
    @Embedded val service: ServiceDb,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryDb
)
