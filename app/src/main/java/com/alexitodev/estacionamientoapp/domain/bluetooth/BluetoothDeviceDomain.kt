package com.alexitodev.estacionamientoapp.domain.bluetooth

data class BluetoothDeviceDomain(
    val name: String?,
    val address: String,
    val rssi: Int
)
