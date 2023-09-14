package com.merkost.suby.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.merkost.suby.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AppSettingsImpl(private val context: Context) : AppSettings {

    // to make sure there's only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("appSettings")

        private val FIRST_TIME_OPENING = booleanPreferencesKey("first_time_opening")
        private val MAIN_CURRENCY = stringPreferencesKey("main_currency")
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


}