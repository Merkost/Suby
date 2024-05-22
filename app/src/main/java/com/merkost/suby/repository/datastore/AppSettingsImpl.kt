package com.merkost.suby.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.merkost.suby.model.Currency
import com.merkost.suby.repository.ktor.jsonDeserializer
import com.merkost.suby.toSafeLocalDateTime
import io.github.jan.supabase.supabaseJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString


class AppSettingsImpl(private val context: Context) : AppSettings {

    // to make sure there's only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("SubySettings")

        private val FIRST_TIME_OPENING = booleanPreferencesKey("first_time_opening")
        private val MAIN_CURRENCY = stringPreferencesKey("main_currency")

        private val LAST_TOTAL = stringPreferencesKey("last_total")
    }


    override val isFirstTimeLaunch: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[FIRST_TIME_OPENING] ?: true
        }

    override suspend fun saveFirstTimeLaunch(newValue: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_TIME_OPENING] = newValue
        }
    }

    override val mainCurrency: Flow<Currency> = context.dataStore.data
        .map { preferences ->
            Currency.find(preferences[MAIN_CURRENCY])
        }

    override suspend fun saveMainCurrency(currency: Currency) {
        context.dataStore.edit { preferences ->
            preferences[MAIN_CURRENCY] = currency.name
        }
    }

    override val lastTotalPrice: Flow<LastTotalPrice?> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_TOTAL]?.let {
                runCatching { jsonDeserializer.decodeFromString<LastTotalPrice>(it) }.getOrNull()
            }
        }

    override suspend fun saveLastTotalPrice(lastTotalPrice: LastTotalPrice) {
        context.dataStore.edit { preferences ->
            preferences[LAST_TOTAL] = jsonDeserializer.encodeToString(lastTotalPrice)
        }
    }

}
