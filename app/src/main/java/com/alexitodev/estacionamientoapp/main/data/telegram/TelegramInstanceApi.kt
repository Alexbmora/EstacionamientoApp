package com.alexitodev.estacionamientoapp.main.data.telegram

import com.alexitodev.estacionamientoapp.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TelegramInstanceApi{
    companion object {
        private val TOKEN = BuildConfig.TELEGRAM_API_TOKEN
        private val BASE_URL = "https://api.telegram.org/bot$TOKEN/"

        val telegramInstanceApi: TelegramInstanceApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TelegramInstanceApi::class.java)
        }
    }

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
