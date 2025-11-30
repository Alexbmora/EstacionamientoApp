package com.alexitodev.estacionamientoapp.main.domain.telegram

import androidx.compose.ui.semantics.text
import com.alexitodev.estacionamientoapp.main.data.telegram.TelegramMessage
import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LogEntry
import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LogLevel
import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LogSource
import com.alexitodev.estacionamientoapp.main.domain.telegram.logger.LoggerRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * Contiene la lógica de negocio para interpretar y responder a los comandos del bot de Telegram.
 * Se instancia manualmente, sin inyección de dependencias.
 */
class TelegramBot(
    private val telegramRepository: TelegramRepository, // Usamos la interfaz del dominio
    private val loggerRepository: LoggerRepository
) {
    // Enum para controlar el flujo de registro de invitados
    private enum class GuestState { NONE, AWAITING_NAME, AWAITING_BUILDING }

    private var lastUpdateId = 0L // El ID puede ser Long
    private var isShiftActive = false
    private var shiftStartTime: String? = null

    // Mapas para manejar el estado de múltiples conversaciones simultáneamente
    private val guestStateByChat = mutableMapOf<Long, GuestState>()
    private val guestNameByChat = mutableMapOf<Long, String>()

    // Formateadores de fecha y hora
    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    /**
     * Método principal que se llama periódicamente para buscar y procesar nuevos mensajes.
     */
    suspend fun checkAndHandleNewMessages() {
        // Obtenemos las actualizaciones usando el repositorio.
        val result = telegramRepository.getUpdates(lastUpdateId + 1)
        result.onSuccess { updates ->
            // Filtramos solo los mensajes que no hemos procesado aún.
            val newUpdates = updates.filter { it.updateId > lastUpdateId }

            if (newUpdates.isNotEmpty()) {
                // Actualizamos el ID del último mensaje procesado.
                lastUpdateId = newUpdates.maxOf { it.updateId }.toLong()

                loggerRepository.addLog(
                    LogEntry(
                        "Se recibieron ${newUpdates.size} nuevos mensajes de Telegram.",
                        LogSource.TELEGRAM,
                        LogLevel.INFO,
                        System.currentTimeMillis()
                    )
                )

                // Procesamos cada mensaje nuevo.
                newUpdates.forEach { update ->
                    update.message?.let { handleMessage(it) }
                }
            }
        }.onFailure { error ->
            // El logging de errores ya se hace en el Repository, pero podemos añadir uno aquí si queremos más contexto.
            loggerRepository.addLog(
                LogEntry(
                    "Fallo al obtener actualizaciones: ${error.message}",
                    LogSource.TELEGRAM,
                    LogLevel.ERROR,
                    System.currentTimeMillis()
                )
            )
        }
    }

    private suspend fun handleMessage(message: TelegramMessage) {
        val chatId = message.chat.id
        val originalText = message.text.trim()
        val textUpper = originalText.uppercase()

        // 1. Manejo del flujo de registro de invitados (máquina de estados)
        when (guestStateByChat.getOrDefault(chatId, GuestState.NONE)) {
            GuestState.AWAITING_NAME -> {
                val guestName = originalText
                guestNameByChat[chatId] = guestName
                guestStateByChat[chatId] = GuestState.AWAITING_BUILDING
                telegramRepository.sendMessage(
                    chatId,
                    "2.- 🏢 Ahora ingresa el EDIFICIO o área a la que se dirige el invitado."
                )
                return // Salimos para esperar la siguiente respuesta
            }

            GuestState.AWAITING_BUILDING -> {
                val building = originalText
                val guestName = guestNameByChat[chatId] ?: "Desconocido"
                val dateTime = dateTimeFormatter.format(Date())

                // Limpiamos el estado para este chat
                guestStateByChat[chatId] = GuestState.NONE
                guestNameByChat.remove(chatId)

                val confirmationMessage = "3.- ✅ Invitado registrado:\n" +
                        "• Nombre: $guestName\n" +
                        "• Edificio: $building\n" +
                        "• Fecha y hora: $dateTime\n" +
                        "Registro almacenado."
                telegramRepository.sendMessage(chatId, confirmationMessage)
                return // Salimos tras completar el flujo
            }

            else -> {} // No está en un flujo, procesar comandos normalmente.
        }

        // 2. Manejo de comandos principales
        val responseText = when (textUpper) {
            "INICIO" -> {
                if (isShiftActive) {
                    "Ya hay un turno iniciado. Escribe TERMINAR para finalizarlo."
                } else {
                    isShiftActive = true
                    shiftStartTime = timeFormatter.format(Date())
                    "✅ Servicio iniciado a las $shiftStartTime. ¡Buen turno! 👮‍♂️"
                }
            }

            "TERMINAR" -> {
                if (!isShiftActive) {
                    "No hay ningún turno iniciado. Escribe INICIO para comenzar."
                } else {
                    val endTime = timeFormatter.format(Date())
                    isShiftActive = false
                    "✅ Servicio finalizado a las $endTime. ¡Gracias por tu trabajo!"
                }
            }

            "IDENTIFICAR" -> "📷 FOTO TOMADA. Enviando para reconocimiento...\nResultado: ${
                listOf(
                    "ALUMNO",
                    "DOCENTE",
                    "PERSONAL",
                    "NO RECONOCIBLE"
                ).random()
            }"

            "INVITADO" -> {
                guestStateByChat[chatId] = GuestState.AWAITING_NAME // Inicia el flujo
                "1.- ✍ Por favor, ingresa el NOMBRE completo del invitado."
            }

            "REPORTE" -> "📊 Resumen (simulado):\nAlumnos: 10\nDocentes: 3\nPersonal: 5\nInvitados: 2"
            else -> "No entiendo ese comando. Usa: INICIO, IDENTIFICAR, INVITADO, REPORTE o TERMINAR."
        }
        telegramRepository.sendMessage(chatId, responseText)
    }
}