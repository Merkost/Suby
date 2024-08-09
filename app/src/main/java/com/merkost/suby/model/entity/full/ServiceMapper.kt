package com.merkost.suby.model.entity.full

import com.merkost.suby.model.room.entity.related.CustomServiceWithCategory
import com.merkost.suby.model.room.entity.related.ServiceWithCategory

fun CustomServiceWithCategory.toService() =
    Service(
        id = customService.id,
        name = customService.name,
        isCustomService = true,
        logoUrl = customService.imageUri,
        createdAt = customService.createdAt,
        lastUpdated = customService.lastUpdated,
        category = Category(
            id = category.id,
            name = category.name,
            emoji = category.emoji,
            createdAt = category.createdAt,
            lastUpdated = category.lastUpdated
        ),
    )

fun ServiceWithCategory.toService() =
    Service(
        id = service.id,
        name = service.name,
        isCustomService = false,
        logoUrl = service.logoLink,
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