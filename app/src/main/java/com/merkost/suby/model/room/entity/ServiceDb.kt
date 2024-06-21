package com.merkost.suby.model.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.merkost.suby.repository.ktor.supaClient
import com.merkost.suby.utils.now
import io.github.jan.supabase.storage.storage
import kotlinx.datetime.LocalDateTime
import timber.log.Timber

@Entity(
    tableName = "service",
    foreignKeys = [
        ForeignKey(
            entity = CategoryDb::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class ServiceDb(
    @PrimaryKey
    val id: Int,
    val name: String,
    val categoryId: Int,
    val logoId: String?,
    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
) {

    private val logoPath: String
        get() = name.replace(' ', '_') + "_logo.svg"

    val logoLink: String?
        get() = logoId?.let {
            runCatching { supaClient.storage["service_logo"].publicUrl(logoPath) }
                .getOrElse {
                    Timber.w(it, "Failed to get logo link for service $name")
                    null
                }
        }
}