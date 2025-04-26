package com.merkost.suby.model.room

import com.merkost.suby.model.entity.dto.CategoryDto
import com.merkost.suby.model.entity.dto.ServiceDto
import com.merkost.suby.model.room.entity.CategoryDb
import com.merkost.suby.model.room.entity.Service

object DbMapper {

    fun mapCategory(it: CategoryDto) =
        CategoryDb(
            id = it.id,
            name = it.name,
            emoji = it.emoji,
            createdAt = it.createdAt
        )

    fun mapService(it: ServiceDto) = Service(
        backendId = it.id,
        name = it.name,
        categoryId = it.categoryId,
        createdAt = it.createdAt,
        logoName = it.logoName?.trim()
    )

}