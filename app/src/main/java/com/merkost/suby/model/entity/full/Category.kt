package com.merkost.suby.model.entity.full

import com.merkost.suby.utils.now
import kotlinx.datetime.LocalDateTime

data class Category(
    val id: Int,
    val name: String,
    val emoji: String,
    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
) {
    val beautifulName: String
    get() = "$name $emoji"
}


