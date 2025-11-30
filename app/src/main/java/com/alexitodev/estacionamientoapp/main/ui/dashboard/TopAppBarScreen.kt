package com.alexitodev.estacionamientoapp.main.ui.dashboard

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.alexitodev.estacionamientoapp.main.ui.theme.AppTheme

@Preview(showBackground = true)
@Composable
fun MainTopAppBarPreview() {
    AppTheme(darkTheme = false) {
        MainTopAppBar(isDarkMode = false, onThemeChange = {}, onMenuClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun MainTopAppBarDarkPreview() {
    AppTheme(darkTheme = true) {
        MainTopAppBar(isDarkMode = true, onThemeChange = {}, onMenuClick = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Monitor de Control de Acceso") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú"
                )
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.Bedtime else Icons.Default.WbSunny,
                    contentDescription = "Cambiar tema"
                )
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onThemeChange
                )
            }
        }
    )
}