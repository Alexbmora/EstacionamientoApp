package com.alexitodev.estacionamientoapp.data.telegram

import android.util.Log
import com.alexitodev.estacionamientoapp.domain.telegram.ITelegramRepository
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LogEntry
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LogLevel
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LogSource
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LoggerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException
import javax.inject.Inject

class TelegramRepository @Inject constructor(
    private val telegramApi: TelegramInstanceApi,
    private val logger: LoggerRepository
) : ITelegramRepository {

    override suspend fun getUpdates(offset: Long): Result<List<TelegramUpdate>> {
        logger.addLog(
            LogEntry(
                message = "Consultando actualizaciones de Telegram...",
                source = LogSource.TELEGRAM,
                level = LogLevel.INFO,
                timestamp = System.currentTimeMillis()
            )
        )
        return withContext(Dispatchers.IO) {
            try {
                val response = telegramApi.getUpdates(offset)
                Log.d("TelegramRepository", "Response: ${response.result}")

                if (response.ok) {
                    logger.addLog(
                        LogEntry(
                            message = "Se recibieron ${response.result.size} actualizaciones.",
                            source = LogSource.TELEGRAM,
                            level = LogLevel.SUCCESS,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    Result.success(response.result)
                } else {
                    val errorMsg = "La API de Telegram devolvió un error (ok=false)"
                    logger.addLog(
                        LogEntry(
                            message = errorMsg,
                            source = LogSource.TELEGRAM,
                            level = LogLevel.ERROR,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    Result.failure(Exception(errorMsg))
                }
            } catch (_: SocketTimeoutException) {
                // Un timeout en Long Polling es NORMAL. Significa que no hubo mensajes.
                // Devolvemos una lista vacía para que el bot simplemente continúe.
                Log.d("TelegramRepository", "Long polling timeout. No new messages.")
                Result.success(emptyList()) // Devolvemos éxito con una lista vacía
            }
            catch (e: HttpException) {
                val errorMsg = "Error en la respuesta del servidor: ${e.code()}"
                logger.addLog(
                    LogEntry(
                        message = errorMsg,
                        source = LogSource.SERVER,
                        level = LogLevel.ERROR,
                        timestamp = System.currentTimeMillis()
                    )
                )
                Result.failure(Exception(errorMsg, e))
            } catch (e: Exception) {
                val errorMsg = "Ocurrió un error inesperado al consultar updates."
                logger.addLog(
                    LogEntry(
                        message = "$errorMsg: ${e.message}",
                        source = LogSource.SYSTEM,
                        level = LogLevel.ERROR,
                        timestamp = System.currentTimeMillis()
                    )
                )
                Result.failure(Exception(errorMsg, e))
            }
        }
    }

    override suspend fun sendMessage(chatId: Long, text: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = telegramApi.sendMessage(chatId, text)
                if (response.ok) {
                    Result.success(Unit)
                } else {
                    val errorMsg = "Error al enviar mensaje a Telegram"
                    logger.addLog(
                        LogEntry(
                            message = errorMsg,
                            source = LogSource.TELEGRAM,
                            level = LogLevel.ERROR,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: HttpException) {
                val errorMsg = "Error de red al enviar mensaje: ${e.code()}"
                logger.addLog(
                    LogEntry(
                        message = errorMsg,
                        source = LogSource.SERVER,
                        level = LogLevel.ERROR,
                        timestamp = System.currentTimeMillis()
                    )
                )
                Result.failure(Exception(errorMsg, e))
            } catch (e: Exception) {
                val errorMsg = "Error inesperado al enviar mensaje."
                logger.addLog(
                    LogEntry(
                        message = "$errorMsg: ${e.message}",
                        source = LogSource.SYSTEM,
                        level = LogLevel.ERROR,
                        timestamp = System.currentTimeMillis()
                    )
                )
                Result.failure(Exception(errorMsg, e))
            }
        }
    }
}
