package com.alexitodev.estacionamientoapp.domain.telegram

import com.alexitodev.estacionamientoapp.data.telegram.TelegramUpdate

interface TelegramRepository {
    suspend fun getUpdates(offset: Long): Result<List<TelegramUpdate>>
    suspend fun sendMessage(chatId: Long, text: String): Result<Unit>
}