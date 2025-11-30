package com.alexitodev.estacionamientoapp.main.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.alexitodev.estacionamientoapp.main.data.telegram.TelegramInstanceApi
import com.alexitodev.estacionamientoapp.main.data.telegram.TelegramRepository
import com.alexitodev.estacionamientoapp.main.domain.SystemStatusManager
import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LoggerRepository

class AppWorkerFactory(
    private val loggerRepository: LoggerRepository,
    private val systemStatusManager: SystemStatusManager
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        // 2. El jefe de obra le pregunta al capataz: "¿Puedes construir un 'TelegramWorker'?"
        return when (workerClassName) {
            TelegramWorker::class.java.name -> {
                // El capataz responde: "¡Claro! Yo sé lo que necesita".

                // 3. El capataz crea las dependencias que son SOLO para este trabajador.
                val telegramApi = TelegramInstanceApi.telegramInstanceApi
                val telegramRepository = TelegramRepository(telegramApi, loggerRepository)

                // 4. El capataz contrata al trabajador especializado, dándole TODO lo que necesita.
                TelegramWorker(
                    appContext,
                    workerParameters,
                    telegramRepository,
                    systemStatusManager,
                    loggerRepository
                )
            }
            // Aquí podemos añadir más trabajadores en el futuro.
            // BluetoothWorker::class.java.name -> { ... }
            else -> null
        }
    }
}