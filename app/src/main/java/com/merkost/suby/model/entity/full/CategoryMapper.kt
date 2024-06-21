package com.merkost.suby.model.entity.full

import com.merkost.suby.model.room.entity.CategoryDb

fun CategoryDb.toCategory() = Category(
    id = id,
    name = name,
    emoji = emoji,
    createdAt = createdAt,
    lastUpdated = lastUpdated
)