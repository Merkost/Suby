package com.merkost.suby.model.entity.full

import kotlinx.datetime.LocalDateTime

data class Service(
    val id: Int = 0,
    val name: String,
    val logoUrl: String?,
    val isCustomService: Boolean,
    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime,
    val category: Category,
)