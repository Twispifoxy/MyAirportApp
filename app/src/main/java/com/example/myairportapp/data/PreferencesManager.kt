package com.example.myairportapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesManager(private val context: Context) {

    companion object {
        private const val DATASTORE_NAME = "settings"
        private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)
        private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
    }

    val searchQueryFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[SEARCH_QUERY_KEY] ?: "" }

    suspend fun saveSearchQuery(query: String) {
        context.dataStore.edit { prefs ->
            prefs[SEARCH_QUERY_KEY] = query
        }
    }
}