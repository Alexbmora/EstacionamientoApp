package com.alexitodev.estacionamientoapp.data.bluetooth
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.alexitodev.estacionamientoapp.domain.bluetooth.BluetoothDeviceDomain

/**
 * BroadcastReceiver que escucha eventos del sistema relacionados con el descubrimiento
 * de dispositivos Bluetooth Clásico.
 *
 * @param onDeviceFound Es una función lambda que se ejecutará cada vez que
 * se encuentre un nuevo dispositivo, pasándolo como un `BluetoothDeviceDomain`.
 */
class FoundDeviceReceiver(
    private val onDeviceFound: (BluetoothDeviceDomain) -> Unit
) : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        // Nos aseguramos de que la acción sea la que nos interesa: ACTION_FOUND
        when (intent.action) {
            BluetoothDevice.ACTION_FOUND -> {
                // Obtenemos el objeto BluetoothDevice del intent.
                // La forma de hacerlo cambia a partir de Android 13 (Tiramisu).
                val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                } else {
                    // Para versiones anteriores, se usa el método deprecado.
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }

                device?.let {
                    // Antes de acceder al nombre, que requiere BLUETOOTH_CONNECT,
                    // verificamos si tenemos el permiso.
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val deviceName = it.name ?: "Dispositivo Clásico sin Nombre"
                        Log.d("FoundDeviceReceiver", "Dispositivo CLÁSICO encontrado: $deviceName (${it.address})")

                        // Ejecutamos la lambda que nos pasaron en el constructor,
                        // entregando el dispositivo encontrado ya convertido a nuestro modelo de dominio.
                        onDeviceFound(
                            BluetoothDeviceDomain(
                                name = deviceName,
                                address = it.address,
                                // El escaneo clásico no devuelve RSSI, así que usamos un valor por defecto
                                // para mantener la consistencia del modelo.
                                rssi = -100
                            )
                        )
                    }
                }
            }
        }
    }
}