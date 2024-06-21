package com.merkost.suby.model.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.merkost.suby.utils.now
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "category")
data class CategoryDb(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val emoji: String,
    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
)