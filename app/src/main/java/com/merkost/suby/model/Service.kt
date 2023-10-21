package com.merkost.suby.model

import android.os.Parcelable
import android.util.Log
import androidx.room.Embedded
import androidx.room.PrimaryKey
import com.merkost.suby.repository.ktor.supaClient
import io.github.jan.supabase.storage.storage
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Service(
    @PrimaryKey
    @SerialName("id") val serviceId: Int,
    @SerialName("name") val name: String,
    @Embedded
    @SerialName("category") val category: Category,
    @SerialName("logo_id") val logoId: String?,
) : Parcelable {
    private val logoPath: String
        get() = name.replace(' ', '_') + "_logo.svg"

    val logoLink: String?
        get() = logoId?.let {
            val url = supaClient.storage.get("service_logo").publicUrl(logoPath)
            Log.e("URL", url)
            url
        }
}