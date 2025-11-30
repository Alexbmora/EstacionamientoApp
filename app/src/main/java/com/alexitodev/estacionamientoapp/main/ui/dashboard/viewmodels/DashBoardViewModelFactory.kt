package com.alexitodev.estacionamientoapp.main.ui.dashboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexitodev.estacionamientoapp.main.di.AppContainer

@Suppress("UNCHECKED_CAST")
class DashboardViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {

            // Obtenemos SOLO las dependencias que el ViewModel necesita.
            val systemStatusManager = AppContainer.systemStatusManager

            // --- ¡CORRECCIÓN! ---
            // Creamos el ViewModel solo con systemStatusManager
            return DashboardViewModel(systemStatusManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}