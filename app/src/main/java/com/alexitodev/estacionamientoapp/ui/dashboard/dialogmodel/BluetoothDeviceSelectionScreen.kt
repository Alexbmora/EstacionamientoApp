package com.alexitodev.estacionamientoapp.ui.dashboard.dialogmodel


import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// Data class de ejemplo para el preview
data class BluetoothDevice(
    val name: String,
    val macAddress: String,
    val signalStrength: Int // Usaremos un valor de 1 a 3 para la fuerza
)

// Datos de ejemplo para el preview
val sampleDevices = listOf(
    BluetoothDevice("Arduino HC-05", "98:D3:31:FC:2A:1B", 3),
    BluetoothDevice("Arduino HC-06", "00:14:03:05:5B:3C", 2),
    BluetoothDevice("ESP32-DevKit", "A4:CF:12:45:8F:2D", 2),
    BluetoothDevice("HC-05 Module", "00:1A:7D:DA:71:13", 1)
)

@Composable
fun BluetoothDeviceSelectionDialog(
    devices: List<BluetoothDevice>,
    onDismiss: () -> Unit,
    onSearch: () -> Unit,
    onConfirm: (BluetoothDevice?) -> Unit
) {
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // --- Cabecera ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bluetooth,
                            contentDescription = "Bluetooth",
                            tint = colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Seleccionar Dispositivo",
                            style = typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // --- Botón de Búsqueda ---
                Button(
                    onClick = onSearch,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Buscar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar Dispositivos", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // --- Lista de Dispositivos ---
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp), // Para que no ocupe toda la pantalla
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(devices) { device ->
                        DeviceItem(
                            device = device,
                            isSelected = device == selectedDevice,
                            onSelect = { selectedDevice = device }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // --- Botones de Acción ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { onConfirm(selectedDevice) },
                        enabled = selectedDevice != null, // Solo se habilita si hay un dispositivo seleccionado
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceItem(
    device: BluetoothDevice,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = if (isSelected) colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
    val borderColor = if (isSelected) colorScheme.primary else colorScheme.outline.copy(alpha = 0.5f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    tint = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = device.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = device.macAddress, fontSize = 12.sp, color = colorScheme.onSurfaceVariant)
                }
            }
            SignalStrengthIndicator(strength = device.signalStrength)
        }
    }
}

@Composable
fun SignalStrengthIndicator(strength: Int) {
    val color = when (strength) {
        3 -> Color.Green
        2 -> Color.Yellow
        else -> Color.Red
    }
    Icon(
        imageVector = Icons.Default.SignalCellularAlt,
        contentDescription = "Fuerza de la señal",
        tint = color
    )
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BluetoothDeviceSelectionDialogPreview() {
    _root_ide_package_.com.alexitodev.estacionamientoapp.ui.theme.AppTheme(darkTheme = true) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            BluetoothDeviceSelectionDialog(
                devices = sampleDevices,
                onDismiss = {},
                onSearch = {},
                onConfirm = {}
            )
        }
    }
}