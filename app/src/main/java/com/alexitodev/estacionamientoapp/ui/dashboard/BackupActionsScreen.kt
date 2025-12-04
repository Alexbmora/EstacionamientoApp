package com.alexitodev.estacionamientoapp.ui.dashboard.BackupActionsScreen

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexitodev.estacionamientoapp.ui.theme.AppTheme

@Preview(name= "Light Mode", showBackground = true)
@Composable
fun BackupActionsPreviewLight() {
    AppTheme {
        BackupActionsScreen()
    }
}

@Preview(name= "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BackupActionsPreviewDark() {
    AppTheme {
        Surface {
            BackupActionsScreen()
        }
    }
}


@Composable
fun BackupActionsScreen() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Acciones de Respaldo",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Forzar Identificación",
                    icon = Icons.Filled.CameraAlt,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    )
                )

                ActionButton(
                    text = "Registrar Invitado",
                    icon = Icons.Filled.PersonAdd,
                    isOutlined = true
                )

                ActionButton(
                    text = "Reiniciar Servicios",
                    icon = Icons.Filled.PowerSettingsNew,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.error,
                        contentColor = colorScheme.onError
                    )
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    isOutlined: Boolean = false
) {
    val buttonModifier = modifier
        .fillMaxWidth()
        .height(50.dp)


    if (isOutlined) {
        OutlinedButton(
            onClick = { /* TODO */ },
            modifier = buttonModifier,
            colors = colors,
            border = BorderStroke(1.dp, colorScheme.outline)
        ) {
            ActionContent(text = text, icon = icon)
        }
    } else {
        Button(
            onClick = { /* TODO */ },
            modifier = buttonModifier,
            colors = colors
        ) {
            ActionContent(text = text, icon = icon)
        }
    }
}

@Composable
private fun ActionContent(text: String, icon: ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(ButtonDefaults.IconSize)
    )
    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
    Text(text, fontWeight = FontWeight.Bold)
}
