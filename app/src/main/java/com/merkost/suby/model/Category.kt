package com.merkost.suby.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Category(
    @SerialName("id") val categoryId: Int,
    @SerialName("name") val categoryName: String,
    val emoji: String,
) : Parcelable