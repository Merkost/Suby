package com.merkost.suby.model.entity.dto

import com.merkost.suby.repository.ktor.serializer.ZonedDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class CategoryDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("emoji") val emoji: String,
    @Serializable(with = ZonedDateTimeSerializer::class)
    @SerialName("created_at") val createdAt: ZonedDateTime
)
