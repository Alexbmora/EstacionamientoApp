package com.alexitodev.estacionamientoapp.main.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.alexitodev.estacionamientoapp.R
import com.alexitodev.estacionamientoapp.main.domain.ServiceStatus
import com.alexitodev.estacionamientoapp.main.domain.SystemStatusManager
import com.alexitodev.estacionamientoapp.main.domain.telegram.TelegramBot
import com.alexitodev.estacionamientoapp.main.domain.telegram.TelegramRepository
import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LoggerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
class TelegramWorker(
    appContext: Context, // SIN 'private val'
    params: WorkerParameters, // SIN 'private val'
    private val telegramRepository: TelegramRepository,
    private val systemStatusManager: SystemStatusManager,
    private val loggerRepository: LoggerRepository
) : CoroutineWorker(appContext, params) { // <-- Se pasan aquí

    // 3. El 'applicationContext' ya existe en la clase base CoroutineWorker,
    // por lo que no necesitamos mantener nuestra propia copia.
    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "telegram_polling_channel"
    }

    override suspend fun doWork(): Result {
        systemStatusManager.setTelegramStatus(ServiceStatus.STARTING)

        setForeground(createForegroundInfo())

        val telegramBot = TelegramBot(telegramRepository, loggerRepository)
        systemStatusManager.setTelegramStatus(ServiceStatus.ACTIVE)

        try {
            // Usar 'isActive' es correcto aquí, pero 'coroutineContext.isActive' es más explícito. Ambos funcionan.
            while (coroutineContext.isActive) {
                telegramBot.checkAndHandleNewMessages()
            }
        } finally {
            // Este bloque se ejecuta si el trabajo es cancelado desde fuera.
            systemStatusManager.resetAllStatus()
            systemStatusManager.setSystemRunning(false)
        }

        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Monitor de Estacionamiento Activo")
            .setContentText("Escuchando comandos de Telegram...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        // Esta parte también es crucial y está bien como la tenías
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val serviceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC or
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            ForegroundInfo(NOTIFICATION_ID, notification, serviceType)
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monitoreo de Telegram",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}