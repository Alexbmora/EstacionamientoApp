package com.alexitodev.estacionamientoapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.alexitodev.estacionamientoapp.domain.SystemState
import com.alexitodev.estacionamientoapp.domain.bluetooth.BluetoothDeviceDomain
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LogEntry
import com.alexitodev.estacionamientoapp.ui.dashboard.BackupActionsScreen.BackupActionsScreen
import com.alexitodev.estacionamientoapp.ui.dashboard.LogsSectionScreen
import com.alexitodev.estacionamientoapp.ui.dashboard.MainTopAppBar
import com.alexitodev.estacionamientoapp.ui.dashboard.ServicesSectionScreen
import com.alexitodev.estacionamientoapp.ui.dashboard.StatusCardScreen
import com.alexitodev.estacionamientoapp.ui.dashboard.viewmodels.DashboardViewModel
import com.alexitodev.estacionamientoapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: DashboardViewModel by viewModels()

    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // 1. Verificamos específicamente si el permiso de UBICACIÓN PRECISA fue concedido.
            val isFineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

            // 2. Verificamos los permisos de Bluetooth para Android 12+
            val areBluetoothPermissionsGrantedForS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions[Manifest.permission.BLUETOOTH_SCAN] ?: false &&
                        permissions[Manifest.permission.BLUETOOTH_CONNECT] ?: false
            } else {
                // En versiones anteriores, este chequeo no es necesario si ya tenemos los otros permisos.
                true
            }

            if (isFineLocationGranted && areBluetoothPermissionsGrantedForS) {
                // ¡CASO IDEAL! Tenemos todo lo que necesitamos.
                Toast.makeText(this, "Permisos concedidos correctamente.", Toast.LENGTH_SHORT).show()
            } else if (!isFineLocationGranted) {
                // --- ¡ESTE ES EL CASO CRÍTICO DE HONOR/XIAOMI! ---
                // El usuario negó la ubicación precisa.
                showPreciseLocationRequiredDialog()
            } else {
                // Faltan otros permisos (probablemente de Bluetooth en Android 12+).
                Toast.makeText(this, "Los permisos de 'Dispositivos cercanos' son necesarios.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Definimos y llamamos a la lógica de permisos DENTRO de onCreate.
        // Este es el lugar correcto para ejecutar código.
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Para Android 12 (API 31) y superior
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            // Para versiones ANTERIORES (OPPO, Honor, Xiaomi pre-Android 12)
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH, // El permiso clave que faltaba
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
        // Lanzamos la solicitud de permisos
        requestMultiplePermissionsLauncher.launch(requiredPermissions)

        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            AppTheme(darkTheme = isDarkMode) {
                ViewContainer(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    onThemeChange = { isDarkMode = it }
                )
            }
        }
    }
    /**
     * Muestra un Toast largo y abre los ajustes de la app para que el usuario
     * pueda corregir el permiso de ubicación manualmente.
     */
    private fun showPreciseLocationRequiredDialog() {
        Toast.makeText(
            this,
            "La ubicación PRECISA es necesaria para encontrar dispositivos Bluetooth. Por favor, actívela.",
            Toast.LENGTH_LONG
        ).show()

        // Abrimos los ajustes de la aplicación para que el usuario pueda cambiar el permiso.
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}

@Composable
fun ViewContainer(
    viewModel: DashboardViewModel?,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val systemState by viewModel?.systemState?.collectAsState(initial = SystemState()) ?: remember { mutableStateOf(
        SystemState()
    ) }
    val logs by viewModel?.logs?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    val scannedDevices by viewModel?.scannedDevices?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList())}

    // Lanzador para el permiso de notificaciones
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel?.startWork() // Si se concede, iniciamos el trabajo
            } else {
                Toast.makeText(context, "El permiso de notificación es necesario para el monitoreo.", Toast.LENGTH_LONG).show()
            }
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainTopAppBar(
                isDarkMode = isDarkMode,
                onThemeChange = onThemeChange,
                onMenuClick = {
                    Toast.makeText(context, "Menú presionado", Toast.LENGTH_SHORT).show()
                }
            )
        },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Content(
                    systemState = systemState,
                    logs = logs,
                    onStartStopClick = {
                        if (systemState.isSystemRunning) {
                            // La UI solo llama a la función del ViewModel
                            viewModel?.stopWork()
                        } else {
                            // La UI solo llama a la función del ViewModel o pide el permiso
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
                                    PackageManager.PERMISSION_GRANTED -> {
                                        viewModel?.startWork()
                                    }
                                    else -> {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            } else {
                                viewModel?.startWork()
                            }
                        }
                    },
                    scannedDevices = scannedDevices,
                    viewModel = viewModel
                )
            }
        }
    )
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    systemState: SystemState,
    logs: List<LogEntry>,
    onStartStopClick: () -> Unit,
    scannedDevices: List<BluetoothDeviceDomain>,
    viewModel: DashboardViewModel?
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        StatusCardScreen(
            telegramStatus = systemState.telegramStatus,
            bluetoothStatus = systemState.bluetoothStatus,
            apiServerStatus = systemState.apiServerStatus
        )
        LogsSectionScreen(logs = logs)
        ServicesSectionScreen(
            isSystemRunning = systemState.isSystemRunning,
            onStartStopClick = onStartStopClick,
            devices = scannedDevices, // <- PASAR AQUÍ
            onStartScan = { viewModel?.startBleScan() }, // <- PASAR AQUÍ
            onStopScan = { viewModel?.stopBleScan() }, // <- PASAR AQUÍ
            onDeviceSelected = { device ->
                // TODO: Conectar al dispositivo. Por ahora, un Toast.
                viewModel?.stopBleScan() // Detenemos el escaneo al seleccionar.
                Toast.makeText(context, "Dispositivo seleccionado: ${device.name}", Toast.LENGTH_SHORT).show()
            }

        )
        BackupActionsScreen()
    }
}
