package com.merkost.suby.model.entity.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("emoji") val emoji: String,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    @SerialName("created_at") val createdAt: LocalDateTime
)
