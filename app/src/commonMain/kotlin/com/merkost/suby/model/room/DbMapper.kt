package com.merkost.suby.model.room

import com.merkost.suby.model.entity.dto.CategoryDto
import com.merkost.suby.model.entity.dto.ServiceDto
import com.merkost.suby.model.room.entity.CategoryDb
import com.merkost.suby.model.room.entity.ServiceDb
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DbMapper {

    fun mapCategory(it: CategoryDto) =
        CategoryDb(
            id = it.id,
            name = it.name,
            emoji = it.emoji,
            createdAt = it.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
        )

    fun mapService(it: ServiceDto) = ServiceDb(
        backendId = it.id,
        name = it.name,
        categoryId = it.categoryId,
        createdAt = it.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()),
        logoName = it.logoName?.trim()
    )

}