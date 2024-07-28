package com.merkost.suby.model.entity.dto

import com.merkost.suby.repository.ktor.serializer.ZonedDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class ServiceDto(

    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("category_id")
    val categoryId: Int,

    @SerialName("logo_name")
    val logoName: String? = null,

    @SerialName("created_at")
    @Serializable(with = ZonedDateTimeSerializer::class)
    val createdAt: ZonedDateTime,
)