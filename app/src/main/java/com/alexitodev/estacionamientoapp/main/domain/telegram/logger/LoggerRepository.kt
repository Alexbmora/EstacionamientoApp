package com.alexitodev.estacionamientoapp.main.domain.telegram.logger

import kotlinx.coroutines.flow.StateFlow

interface LoggerRepository {
    val logs: StateFlow<List<LogEntry>>
    fun addLog(logEntry: LogEntry)
}
