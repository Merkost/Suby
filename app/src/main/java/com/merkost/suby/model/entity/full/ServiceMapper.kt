package com.merkost.suby.model.entity.full

import com.merkost.suby.model.room.entity.related.ServiceWithCategory

fun ServiceWithCategory.toService() =
    Service(
        id = service.id,
        name = service.name,
        isCustomService = service.backendId == null,
        logoUrl = service.imageLink,
        createdAt = service.createdAt,
        lastUpdated = service.lastUpdated,
        category = Category(
            id = category.id,
            name = category.name,
            emoji = category.emoji,
            createdAt = category.createdAt,
            lastUpdated = category.lastUpdated
        )
    )