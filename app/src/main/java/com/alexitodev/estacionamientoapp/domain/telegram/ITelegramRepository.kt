package com.alexitodev.estacionamientoapp.domain.telegram

import com.alexitodev.estacionamientoapp.data.telegram.TelegramUpdate

interface ITelegramRepository {
    suspend fun getUpdates(offset: Long): Result<List<TelegramUpdate>>
    suspend fun sendMessage(chatId: Long, text: String): Result<Unit>
}