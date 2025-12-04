package com.alexitodev.estacionamientoapp.ui.dashboard.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.alexitodev.estacionamientoapp.data.bluetooth.BleManager
import com.alexitodev.estacionamientoapp.domain.SystemStatusManager
import com.alexitodev.estacionamientoapp.work.TelegramWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val systemStatusManager: SystemStatusManager,
    private val bleManager: BleManager,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val systemState = systemStatusManager.systemState
    val logs = systemStatusManager.logs

    val scannedDevices = bleManager.scannedDevices
    private val workManager = WorkManager.getInstance(context)
    private val workName = "telegram_polling_work"

    init {
        checkInitialWorkStatus()
    }

    fun startBleScan() {
        bleManager.startScan()
    }

    fun stopBleScan() {
        bleManager.stopScan()
    }

    private fun checkInitialWorkStatus() {
        viewModelScope.launch {
            val isRunning = withContext(Dispatchers.IO) {
                val workInfos = workManager.getWorkInfosForUniqueWork(workName).get()
                workInfos?.any { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED } ?: false
            }
            systemStatusManager.setSystemRunning(isRunning)
        }
    }

    fun startWork() {
        val workRequest = OneTimeWorkRequestBuilder<TelegramWorker>().build()
        workManager.enqueueUniqueWork(workName, ExistingWorkPolicy.KEEP, workRequest)
        setSystemRunning(true)
        Toast.makeText(context, "Iniciando monitoreo...", Toast.LENGTH_SHORT).show()
    }

    fun stopWork() {
        workManager.cancelUniqueWork(workName)
        Toast.makeText(context, "Deteniendo monitoreo...", Toast.LENGTH_SHORT).show()
        setSystemRunning(isRunning = false)
        systemStatusManager.resetAllStatus()
    }

    fun setSystemRunning(isRunning: Boolean) {
        systemStatusManager.setSystemRunning(isRunning)
    }
}