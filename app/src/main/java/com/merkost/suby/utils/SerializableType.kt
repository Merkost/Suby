@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.merkost.suby.utils

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.json.Json

inline fun <reified T> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: SavedState, key: String) =
        bundle.read {
            this.getStringOrNull(key)?.let<String, T>(json::decodeFromString)
        }

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String = Uri.encode(json.encodeToString(value))

    override fun put(bundle: SavedState, key: String, value: T) {
        bundle.write {
            this.putString(key, json.encodeToString(value))
        }
    }
}