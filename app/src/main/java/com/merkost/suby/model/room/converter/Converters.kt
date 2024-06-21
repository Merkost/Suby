package com.merkost.suby.model.room.converter

import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class Converters {

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime?): String? {
        return localDateTime?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return kotlin.runCatching { LocalDateTime.parse(dateTimeString!!) }.getOrNull()
    }

    @TypeConverter
    fun fromLocalDate(localDate: LocalDate?): String? {
        return localDate?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return kotlin.runCatching { LocalDate.parse(dateString!!) }.getOrNull()
    }

    @TypeConverter
    fun fromColor(color: Color?): Int? {
        return color?.value?.toInt()
    }

    @TypeConverter
    fun toColor(colorInt: Int?): Color? {
        return colorInt?.let { Color(it) }
    }
}