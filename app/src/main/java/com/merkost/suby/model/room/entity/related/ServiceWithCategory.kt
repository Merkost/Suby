package com.merkost.suby.model.room.entity.related

import androidx.room.Embedded
import androidx.room.Relation
import com.merkost.suby.model.room.entity.CategoryDb
import com.merkost.suby.model.room.entity.Service

data class ServiceWithCategory(
    @Embedded val service: Service,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryDb
)
