package com.alexitodev.estacionamientoapp.ui.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexitodev.estacionamientoapp.domain.ServiceStatus
import com.alexitodev.estacionamientoapp.ui.theme.AppTheme


@Preview(name = "Light Mode", showBackground = true)
@Composable
fun StatusCardScreenLightPreview() {
    AppTheme {
        Surface {
            // La preview ahora también pasa los nuevos parámetros
            StatusCardScreen(
                telegramStatus = ServiceStatus.ACTIVE,
                bluetoothStatus = ServiceStatus.INACTIVE,
                apiServerStatus = ServiceStatus.ERROR
            )
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun StatusCardScreenDarkPreview() {
    AppTheme {
        Surface {
            StatusCardScreen(
                telegramStatus = ServiceStatus.ACTIVE,
                bluetoothStatus = ServiceStatus.INACTIVE,
                apiServerStatus = ServiceStatus.ERROR
            )
        }
    }
}

@Composable
fun StatusCardScreen(
    // 2. ACEPTA LOS ESTADOS COMO PARÁMETROS
    telegramStatus: ServiceStatus,
    bluetoothStatus: ServiceStatus,
    apiServerStatus: ServiceStatus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 3. PASA EL ESTADO CORRESPONDIENTE A CADA TARJETA
        StatusCard(modifier = Modifier.weight(1f), serviceName = "Telegram Bot", status = telegramStatus)
        StatusCard(modifier = Modifier.weight(1f), serviceName = "Bluetooth", status = bluetoothStatus)
        StatusCard(modifier = Modifier.weight(1f), serviceName = "Api Servidor", status = apiServerStatus)
    }
}

@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    serviceName: String,
    // 4. EL PARÁMETRO status AHORA ES DEL TIPO ServiceStatus
    status: ServiceStatus,
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        // 5. LA LÓGICA USA EL ENUM PARA DETERMINAR EL COLOR
                        when (status) {
                            ServiceStatus.ACTIVE -> Color.Green
                            ServiceStatus.INACTIVE -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            ServiceStatus.STARTING -> Color(0xFFFFC107) // Un color ámbar para "Iniciando"
                            ServiceStatus.ERROR -> Color.Red
                        }
                    )
            )
            Text(
                text = serviceName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                // 6. LA LÓGICA USA EL ENUM PARA DETERMINAR EL TEXTO A MOSTRAR
                text = when(status) {
                    ServiceStatus.ACTIVE -> "Activo"
                    ServiceStatus.INACTIVE -> "Inactivo"
                    ServiceStatus.STARTING -> "Iniciando..."
                    ServiceStatus.ERROR -> "Error"
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}
