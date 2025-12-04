package com.alexitodev.estacionamientoapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val LAST_UPDATE_ID_KEY = longPreferencesKey("last_telegram_update_id")

    suspend fun saveLastUpdateId(id: Long) {
        context.dataStore.edit { settings ->
            settings[LAST_UPDATE_ID_KEY] = id
        }
    }

    suspend fun getLastUpdateId(): Long {
        val preferences = context.dataStore.data.first()
        return preferences[LAST_UPDATE_ID_KEY] ?: 0L
    }
}