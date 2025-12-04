package com.alexitodev.estacionamientoapp.data.telegram

import retrofit2.http.GET
import retrofit2.http.Query

interface TelegramInstanceApi{
    @GET("getUpdates")
    suspend fun getUpdates(
        @Query("offset") offset: Long,
        @Query("timeout") timeout: Int = 60
    ) : TelegramUpdatesResponse

    @GET("sendMessage")
    suspend fun sendMessage(
        @Query("chat_id") chatId: Long,
        @Query("text") text: String
    ) : TelegramSendReponse

}
