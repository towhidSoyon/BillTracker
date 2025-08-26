package com.towhid.billtracker.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sub_prefs")

class UserPrefs(private val context: Context) {
    private val KEY_CURRENCY = stringPreferencesKey("preferred_currency")

    val preferredCurrency: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_CURRENCY] ?: "BDT"
    }

    suspend fun setPreferredCurrency(code: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CURRENCY] = code
        }
    }
}