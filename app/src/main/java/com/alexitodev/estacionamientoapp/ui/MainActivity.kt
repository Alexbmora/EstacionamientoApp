package com.alexitodev.estacionamientoapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    val logs by viewModel?.logs?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList<LogEntry>()) }

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
                    }
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
    onStartStopClick: () -> Unit
) {
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
            onStartStopClick = onStartStopClick
        )
        BackupActionsScreen()
    }
}