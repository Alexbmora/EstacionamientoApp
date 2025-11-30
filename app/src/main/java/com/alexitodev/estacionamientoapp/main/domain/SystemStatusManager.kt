package com.alexitodev.estacionamientoapp.main.domain

import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LogEntry
import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LoggerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

// --- 1. Definimos los estados posibles para cada servicio ---
enum class ServiceStatus {
    ACTIVE,
    INACTIVE,
    ERROR,
    STARTING
}

// --- 2. Modelo de datos que representa el estado completo del sistema ---
data class SystemState(
    val telegramStatus: ServiceStatus = ServiceStatus.INACTIVE,
    val bluetoothStatus: ServiceStatus = ServiceStatus.INACTIVE,
    val apiServerStatus: ServiceStatus = ServiceStatus.INACTIVE,
    val isSystemRunning: Boolean = false // Estado general del botón "Iniciar/Detener Servicio"
)

/**
 * Clase central que gestiona y expone el estado agregado de todos los servicios del sistema.
 * También actúa como fachada para exponer los logs de manera reactiva.
 */
class SystemStatusManager(
    // Recibe el LoggerRepository para poder exponer sus logs
    val loggerRepository: LoggerRepository
) {
    // --- 3. Flujos de Estado (StateFlow) para gestionar y exponer el estado en tiempo real ---

    // _systemState es privado y mutable, solo esta clase puede cambiarlo.
    private val _systemState = MutableStateFlow(SystemState())
    // systemState es público e inmutable, la UI lo observa para actualizarse.
    val systemState: StateFlow<SystemState> = _systemState.asStateFlow()

    // Exponemos directamente el flujo de logs del LoggerRepository.
    // La UI observará este flujo para mostrar la lista de logs.
    val logs: StateFlow<List<LogEntry>> = loggerRepository.logs

    // --- 4. Funciones para actualizar el estado desde otras partes de la app (ViewModels, Servicios, etc.) ---

    fun setTelegramStatus(status: ServiceStatus) {
        _systemState.value = _systemState.value.copy(telegramStatus = status)
    }

    fun setBluetoothStatus(status: ServiceStatus) {
        _systemState.value = _systemState.value.copy(bluetoothStatus = status)
    }

    fun setApiServerStatus(status: ServiceStatus) {
        _systemState.value = _systemState.value.copy(apiServerStatus = status)
    }

    fun setSystemRunning(isRunning: Boolean) {
        _systemState.value = _systemState.value.copy(isSystemRunning = isRunning)
    }

    fun getSystemRunning(): Boolean {
        return _systemState.value.isSystemRunning
    }

    /**
     * Resetea todos los estados a su valor inicial.
     * Útil cuando se detiene el servicio principal.
     */
    fun resetAllStatus() {
        _systemState.value = SystemState() // Restaura al estado por defecto
    }
}