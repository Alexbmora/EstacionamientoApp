package com.alexitodev.estacionamientoapp.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.alexitodev.estacionamientoapp.domain.bluetooth.BluetoothDeviceDomain
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LogEntry
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LogLevel
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LogSource
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LoggerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.System
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@SuppressLint("MissingPermission") // Los permisos se validan en cada función pública
class BleManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: LoggerRepository
) {
    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    val scannedDevices: StateFlow<List<BluetoothDeviceDomain>> = _scannedDevices.asStateFlow()

    private var isScanning = false

    // --- Receptor para Bluetooth Clásico (Discovery) ---
    private var foundDeviceReceiver: FoundDeviceReceiver? = null

    // --- Callback para Bluetooth LE ---
    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                // Quitamos el filtro de nombre nulo para máxima compatibilidad
                val deviceName = device.name ?: "Dispositivo sin Nombre"
                Log.d(
                    "BleManager_LE",
                    "Dispositivo BLE encontrado: $deviceName (${device.address})"
                )
                addOrUpdateDevice(
                    BluetoothDeviceDomain(
                        name = deviceName,
                        address = device.address,
                        rssi = result.rssi
                    )
                )
            }
        }

        override fun onScanFailed(errorCode: Int) {
            // Logueamos el error pero no hacemos nada más, el escaneo clásico puede seguir funcionando
            logger.addLog(
                LogEntry(
                    "Fallo en escaneo BLE, código: $errorCode",
                    LogSource.BLUETOOTH,
                    LogLevel.ERROR,
                    System.currentTimeMillis()
                )
            )
        }
    }

    // --- Función Unificada para añadir dispositivos ---
    private fun addOrUpdateDevice(device: BluetoothDeviceDomain) {
        _scannedDevices.update { currentList ->
            if (currentList.none { it.address == device.address }) {
                // Solo añadimos si es un dispositivo nuevo
                (currentList + device).sortedByDescending { it.name }
            } else {
                currentList // Si no, devolvemos la lista sin cambios para evitar recomposiciones innecesarias
            }
        }
    }

    // --- Orquestador Principal de Escaneo ---
    fun startScan() {
        if (!hasRequiredPermissions()) {
            logger.addLog(
                LogEntry(
                    "Faltan permisos de Bluetooth/Localización.",
                    LogSource.BLUETOOTH,
                    LogLevel.ERROR,
                    System.currentTimeMillis()
                )
            )
            return
        }
        if (bluetoothAdapter?.isEnabled == false) {
            logger.addLog(
                LogEntry(
                    "Bluetooth está desactivado.",
                    LogSource.BLUETOOTH,
                    LogLevel.WARNING,
                    System.currentTimeMillis()
                )
            )
            return
        }
        if (isScanning) return

        isScanning = true
        _scannedDevices.value = emptyList()
        logger.addLog(
            LogEntry(
                "Iniciando escaneo DUAL (BLE + Clásico)...",
                LogSource.BLUETOOTH,
                LogLevel.INFO,
                System.currentTimeMillis()
            )
        )

        // --- 1. Iniciar Escaneo BLE (Modo Súper Compatible) ---
        // Usamos la configuración más simple y estándar posible: sin filtros y modo balanceado.
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED) // Más compatible que LOW_LATENCY
            .build()
        // El primer parámetro es `null` para no darle al sistema del fabricante excusas para filtrar.
        bluetoothAdapter?.bluetoothLeScanner?.startScan(null, settings, leScanCallback)

        // --- 2. Iniciar Escaneo Clásico (Discovery) ---
        // Esto a menudo funciona cuando el escaneo BLE es bloqueado por el fabricante.
        foundDeviceReceiver = FoundDeviceReceiver { device ->
            addOrUpdateDevice(device)
        }.also { receiver ->
            context.registerReceiver(
                receiver,
                IntentFilter(BluetoothDevice.ACTION_FOUND)
            )
        }
        bluetoothAdapter?.startDiscovery()
    }

    fun stopScan() {
        if (!isScanning) return
        isScanning = false
        logger.addLog(
            LogEntry(
                "Deteniendo escaneos...",
                LogSource.BLUETOOTH,
                LogLevel.INFO,
                System.currentTimeMillis()
            )
        )

        // Detener Escaneo BLE
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)

        // Detener Escaneo Clásico
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        foundDeviceReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: Exception) { /* Ignorar si ya no está registrado */
            }
        }
        foundDeviceReceiver = null
    }

    // El chequeo de permisos se mantiene igual
    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            // Para versiones ANTERIORES (la condición que previene el crash)
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        }
    }
}