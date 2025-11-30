package com.alexitodev.estacionamientoapp.main.di

import com.alexitodev.estacionamientoapp.main.data.logger.InMemoryLogger
import com.alexitodev.estacionamientoapp.main.domain.SystemStatusManager

/**
 * Singleton para instanciar y proveer las dependencias de la app manualmente.
 * Esto nos permite compartir las mismas instancias entre el Service y la UI.
 */
object AppContainer {

    // Instancia única del Logger (en memoria)
    val loggerRepository by lazy {
        InMemoryLogger()
    }

    // Instancia única del SystemStatusManager.
    // La UI y el Service observarán y modificarán este mismo objeto.
    val systemStatusManager by lazy {
        SystemStatusManager(loggerRepository)
    }

}