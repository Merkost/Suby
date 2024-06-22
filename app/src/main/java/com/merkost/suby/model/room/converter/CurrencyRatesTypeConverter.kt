package com.merkost.suby.model.room.converter

import androidx.room.TypeConverter
import com.merkost.suby.model.entity.Currency
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CurrencyRatesTypeConverter {

    @TypeConverter
    fun fromJson(jsonString: String): Map<Currency, Double> {
        return Json.decodeFromString(jsonString)
    }

    @TypeConverter
    fun toJson(map: Map<Currency, Double>): String {
        return Json.encodeToString(map)
    }
}