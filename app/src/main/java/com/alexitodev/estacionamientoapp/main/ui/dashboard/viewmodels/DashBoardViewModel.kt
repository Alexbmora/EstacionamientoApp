package com.alexitodev.estacionamientoapp.main.ui.dashboard.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.alexitodev.estacionamientoapp.main.domain.SystemStatusManager
import com.alexitodev.estacionamientoapp.main.work.TelegramWorker
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val systemStatusManager: SystemStatusManager
) : ViewModel() {
    val systemState = systemStatusManager.systemState
    val logs = systemStatusManager.logs // Ya obtenemos los logs a través del SystemStatusManager

    private val workName = "telegram_polling_work"

    // Esta función se llamará automáticamente cuando el ViewModel se cree.
    // Su propósito es mantener la UI sincronizada con el estado real de WorkManager.
    fun checkInitialWorkStatus(context: Context) {
        viewModelScope.launch {
            val workInfos = WorkManager.getInstance(context).getWorkInfosForUniqueWork(workName).get()
            val isRunning = workInfos?.any { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED } ?: false
            systemStatusManager.setSystemRunning(isRunning)
        }
    }
    // NOTA: Para que esto funcione, necesitamos el contexto de la aplicación.
    // Lo llamaremos desde la MainActivity en el siguiente paso.

    fun startWork(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val workRequest = OneTimeWorkRequestBuilder<TelegramWorker>().build()

        // Usamos KEEP para no reemplazar un trabajo que ya podría estar en cola.
        workManager.enqueueUniqueWork(workName, ExistingWorkPolicy.KEEP, workRequest)
        setSystemRunning(true) // Actualizamos la UI inmediatamente
        Toast.makeText(context, "Iniciando monitoreo...", Toast.LENGTH_SHORT).show()
    }

    fun stopWork(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(workName)
        Toast.makeText(context, "Deteniendo monitoreo...", Toast.LENGTH_SHORT).show()
        setSystemRunning(isRunning = false)
        viewModelScope.cancel()
        systemStatusManager.resetAllStatus()
    }

    // --- Funciones para actualizar el estado de la UI ---
    fun setSystemRunning(isRunning: Boolean) {
        systemStatusManager.setSystemRunning(isRunning)
    }

}