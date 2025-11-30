package com.alexitodev.estacionamientoapp.main.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// Creamos una instancia de DataStore para toda la app
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository(private val context: Context) {

    // Definimos una clave para nuestro lastUpdateId
    private val LAST_UPDATE_ID_KEY = longPreferencesKey("last_telegram_update_id")

    // Función para guardar el último ID
    suspend fun saveLastUpdateId(id: Long) {
        context.dataStore.edit { settings ->
            settings[LAST_UPDATE_ID_KEY] = id
        }
    }

    // Función para leer el último ID guardado
    suspend fun getLastUpdateId(): Long {
        val preferences = context.dataStore.data.first()
        return preferences[LAST_UPDATE_ID_KEY] ?: 0L
    }
}