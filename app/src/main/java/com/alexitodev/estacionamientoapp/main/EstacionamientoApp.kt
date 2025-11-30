package com.alexitodev.estacionamientoapp.main

import android.app.Application
import androidx.work.Configuration
import com.alexitodev.estacionamientoapp.main.di.AppContainer
import com.alexitodev.estacionamientoapp.main.work.AppWorkerFactory

class EstacionamientoApp : Application(), Configuration.Provider {
    // La interfaz se implementa con 'override val', no con 'override fun'.
    override val workManagerConfiguration: Configuration
        get() {
            // 1. Obtenemos los recursos compartidos de nuestro contenedor manual.
            val loggerRepository = AppContainer.loggerRepository
            val systemStatusManager = AppContainer.systemStatusManager

            // 2. Creamos al "capataz" (la factory) y le damos los recursos compartidos.
            val workerFactory = AppWorkerFactory(loggerRepository, systemStatusManager)

            // 3. Le decimos al "jefe de obra" (WorkManager) que use a nuestro capataz.
            return Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .setWorkerFactory(workerFactory)
                .build()
        }
}