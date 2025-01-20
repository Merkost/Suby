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
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["backendId"], unique = true)
    ]
)
data class Service(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val backendId: Int? = null,
    val name: String,
    val categoryId: Int,
    val logoName: String? = null,
    val customImageUri: String? = null,
    val isDeprecated: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now()
) {

    /**
     * Computed property to retrieve the appropriate image link.
     * - For backend services, constructs the URL using `logoName`.
     * - For custom services, uses the provided `customImageUri`.
     */
    val imageLink: String?
        get() = if (backendId != null && logoName != null) {
            runCatching { supaClient.storage["service_logo"].publicUrl(logoName.orEmpty()) }
                .getOrElse {
                    Timber.w(it, "Failed to get logo link for service $name")
                    null
                }
        } else {
            customImageUri
        }
}