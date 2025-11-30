package com.alexitodev.estacionamientoapp.main.domain.telegram

import com.alexitodev.estacionamientoapp.main.data.telegram.TelegramUpdate

interface TelegramRepository {
    suspend fun getUpdates(offset: Long): Result<List<TelegramUpdate>>
    suspend fun sendMessage(chatId: Long, text: String): Result<Unit>
}