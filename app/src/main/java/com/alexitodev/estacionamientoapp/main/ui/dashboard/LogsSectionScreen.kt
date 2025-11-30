package com.alexitodev.estacionamientoapp.main.ui.dashboard

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LogEntry
import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LogLevel
import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LogSource
import com.alexitodev.estacionamientoapp.main.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Locale


@Preview( showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun LogsSectionPreview() {
    AppTheme {
        Surface {
            val sampleLogs = listOf(
                LogEntry("Iniciando sistema...", LogSource.SYSTEM, LogLevel.INFO, System.currentTimeMillis()),
                LogEntry("Conexión con Telegram OK", LogSource.TELEGRAM, LogLevel.SUCCESS, System.currentTimeMillis()),
                LogEntry("Error al conectar con Bluetooth: Dispositivo no encontrado", LogSource.BLUETOOTH, LogLevel.ERROR, System.currentTimeMillis()),
                LogEntry("Advertencia: La batería del dispositivo es baja (15%)", LogSource.SYSTEM, LogLevel.WARNING, System.currentTimeMillis()),
                LogEntry("Respuesta no válida del servidor API", LogSource.SERVER, LogLevel.ERROR, System.currentTimeMillis())
            )
            LogsSectionScreen(logs = sampleLogs)
        }
    }
}


@Composable
fun LogsSectionScreen(
    logs: List<LogEntry> // 1. Acepta la lista de logs
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusIndicators()
                Text(
                    text = "Registros del Sistema",
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
            // 2. Pasa la lista al LogBox
            LogBox(logs = logs)
        }
    }
}


@Composable
fun LogBox(
    logs: List<LogEntry> // 3. Acepta la lista de logs
) {
    // Usamos 'remember' para no recrear el formateador en cada recomposición
    val timeFormatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = cardColors(
            containerColor = colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 4. Lógica para mostrar los logs o un mensaje si está vacío
            if (logs.isEmpty()) {
                Text(
                    text = "Aún no hay registros.",
                    fontFamily = FontFamily.Monospace,
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            } else {
                // Itera sobre la lista real de logs y la muestra
                // .take(10) es opcional, para evitar que la lista sea demasiado larga
                logs.take(10).forEach { log ->
                    val time = timeFormatter.format(log.timestamp)
                    val logColor = when(log.level) {
                        LogLevel.ERROR -> Color.Red
                        LogLevel.WARNING -> Color.Yellow.copy(alpha = 0.8f) // Un amarillo más legible
                        else -> colorScheme.onSurface
                    }
                    Text(
                        text = "[$time] > ${log.message}",
                        fontFamily = FontFamily.Monospace,
                        color = logColor,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun StatusIndicators() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color.Red))
        Spacer(modifier = Modifier.width(4.dp))
        Box(modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color.Yellow))
        Spacer(modifier = Modifier.width(4.dp))
        Box(modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color.Green))
    }
}