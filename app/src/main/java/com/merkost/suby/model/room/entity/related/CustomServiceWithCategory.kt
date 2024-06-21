package com.merkost.suby.model.room.entity.related

import androidx.room.Embedded
import androidx.room.Relation
import com.merkost.suby.model.room.entity.CategoryDb
import com.merkost.suby.model.room.entity.CustomServiceDb

data class CustomServiceWithCategory(
    @Embedded val customService: CustomServiceDb,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryDb
)