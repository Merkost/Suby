package com.merkost.suby.model.room.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.merkost.suby.repository.ktor.serializer.ZonedDateTimeSerializer
import com.merkost.suby.repository.ktor.supaClient
import io.github.jan.supabase.storage.storage
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.time.ZonedDateTime

@Parcelize
@Entity(
    tableName = "service",
    foreignKeys = [
        ForeignKey(
            entity = CategoryDb::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class ServiceDb(
    @PrimaryKey
    @ColumnInfo(name = "serviceId")
    val id: Int,
    val name: String,
    val categoryId: Int,
    val logoId: String?,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val createdAt: ZonedDateTime,
) : Parcelable {

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