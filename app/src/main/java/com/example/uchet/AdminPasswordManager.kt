package com.example.uchet

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class AdminPasswordManager(private val context: Context) {
    private val ADMIN_PASSWORD_KEY = stringPreferencesKey("admin_password")
    private val API_URL = stringPreferencesKey("api_url")
    private val API_TOKEN = stringPreferencesKey("api_token")

    suspend fun saveAdminPassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[ADMIN_PASSWORD_KEY] = password
        }
    }

    val adminPassword: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[ADMIN_PASSWORD_KEY]
        }
}