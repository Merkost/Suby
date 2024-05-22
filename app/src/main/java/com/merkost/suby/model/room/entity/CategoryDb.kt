package com.merkost.suby.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = "category")
data class CategoryDb(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("categoryId")
    val id: Int,
    val name: String,
    val emoji: String,
    val createdAt: ZonedDateTime,
    val lastUpdated: Long = System.currentTimeMillis(),
)