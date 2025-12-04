package com.alexitodev.estacionamientoapp.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.alexitodev.estacionamientoapp.R
import com.alexitodev.estacionamientoapp.domain.ServiceStatus
import com.alexitodev.estacionamientoapp.domain.SystemStatusManager
import com.alexitodev.estacionamientoapp.domain.telegram.TelegramBot
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.isActive

@HiltWorker
class TelegramWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val telegramBot: TelegramBot,
    private val systemStatusManager: SystemStatusManager
) : CoroutineWorker(appContext, params) {

    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "telegram_polling_channel"
    }

    override suspend fun doWork(): Result {
        systemStatusManager.setTelegramStatus(ServiceStatus.STARTING)

        setForeground(createForegroundInfo())

        telegramBot.initialize()

        systemStatusManager.setTelegramStatus(ServiceStatus.ACTIVE)

        try {
            while (coroutineContext.isActive) {
                telegramBot.checkAndHandleNewMessages()
            }
        } finally {
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