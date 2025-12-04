package com.alexitodev.estacionamientoapp.data.logger

import com.alexitodev.estacionamientoapp.domain.telegram.logger.LogEntry
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LoggerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update

/**
 * Implementación de LoggerRepository que guarda los logs en una lista en memoria.
 * Su naturaleza es volátil (los logs se pierden si el proceso de la app muere).
 * Usa StateFlow para que la UI pueda suscribirse a los cambios en tiempo real.
 */
class InMemoryLogger : LoggerRepository {
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    override val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    override fun addLog(logEntry: LogEntry) {
        _logs.update { currentLogs ->
            listOf(logEntry) + currentLogs
        }
    }

    fun clearLogs() {
        _logs.update { emptyList() }
    }
}