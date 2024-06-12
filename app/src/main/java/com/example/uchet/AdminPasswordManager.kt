package com.example.uchet

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdminPasswordManager(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

        val LAST_UPDATE = stringPreferencesKey("last_update")
        val LAST_UPLOAD = stringPreferencesKey("last_upload")
        val URL = stringPreferencesKey("url")
        val ADMIN_PASS = stringPreferencesKey("admin_pass")
        val UNIQUE_ID = stringPreferencesKey("unique_id")
        val RFID_NAME = stringPreferencesKey("rfid_name")
    }
    val companion = Companion
    suspend fun saveStringPreference(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    val getLastUpdate: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_UPDATE] ?: ""
        }
    val getRFID: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[RFID_NAME] ?: ""
        }
    val getLastUpload: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_UPLOAD] ?: ""
        }
    val getUrl: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[URL] ?: ""
        }
    val getAdminPass: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[ADMIN_PASS] ?: ""
        }
    val getUniqueId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[UNIQUE_ID] ?: ""
        }
}