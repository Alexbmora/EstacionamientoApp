package com.alexitodev.estacionamientoapp.ui.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexitodev.estacionamientoapp.domain.bluetooth.BluetoothDeviceDomain
import com.alexitodev.estacionamientoapp.ui.dashboard.components.BluetoothDeviceSelectionDialog
import com.alexitodev.estacionamientoapp.ui.theme.AppTheme

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun ServicesSectionLightPreview() {
    AppTheme {
        Surface {
            ServicesSectionScreen(
                isSystemRunning = false,
                onStartStopClick = {},
                devices = listOf(),
                onStartScan = {},
                onStopScan = {},
                onDeviceSelected = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun ServicesSectionDarkPreview() {
    AppTheme {
        Surface {
            ServicesSectionScreen(
                isSystemRunning = false,
                onStartStopClick = {},
                devices = listOf(),
                onStartScan = {},
                onStopScan = {},
                onDeviceSelected = {}
            )
        }
    }
}

@Composable
fun ServicesSectionScreen(
    isSystemRunning: Boolean,
    onStartStopClick: () -> Unit,
    devices: List<BluetoothDeviceDomain>,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onDeviceSelected: (BluetoothDeviceDomain) -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BluetoothDeviceSection(
            devices = devices,
            onStartScan = onStartScan,
            onStopScan = onStopScan,
            onDeviceSelected = onDeviceSelected
        )
        SystemControlSection(
            isSystemRunning = isSystemRunning,
            onStartStopClick = onStartStopClick
        )
    }
}

@Composable
private fun BluetoothDeviceSection(
    devices: List<BluetoothDeviceDomain>,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onDeviceSelected: (BluetoothDeviceDomain) -> Unit
) {
    // 1. Estado para controlar si el diálogo está abierto o cerrado
    var showDialog by remember { mutableStateOf(false) }

    // 2. Si showDialog es true, mostramos nuestro nuevo Composable
    if (showDialog) {
        BluetoothDeviceSelectionDialog(
            devices = devices, // <-- Usa la lista real de dispositivos
            onDismiss = {
                onStopScan() // Detiene el escaneo si se cierra el diálogo
                showDialog = false
            },
            onSearch = onStartScan, // <-- Llama a la función del ViewModel
            onConfirm = { selectedDevice ->
                selectedDevice?.let { onDeviceSelected(it) } // Notifica la selección
                showDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Dispositivo Bluetooth",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "No seleccionado",
                    style = typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                )
                Button(onClick = { showDialog = true }) {
                    Text("Seleccionar")
                }
            }
        }
    }
}

@Composable
private fun SystemControlSection(
    isSystemRunning: Boolean,
    onStartStopClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Control del Sistema",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onStartStopClick, // <--- 4. USA LA ACCIÓN AQUÍ
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    // 5. CAMBIA EL COLOR BASADO EN EL ESTADO
                    containerColor = if (isSystemRunning) Color(0xFFD32F2F) else Color(0xFF4CAF50), // Rojo si está corriendo, Verde si no
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                // 6. CAMBIA EL TEXTO BASADO EN EL ESTADO
                Text(
                    text = if (isSystemRunning) "Detener Servicio" else "Iniciar Servicio",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
