package com.alexitodev.estacionamientoapp.di

import com.alexitodev.estacionamientoapp.BuildConfig
import com.alexitodev.estacionamientoapp.data.logger.InMemoryLogger
import com.alexitodev.estacionamientoapp.data.telegram.TelegramInstanceApi
import com.alexitodev.estacionamientoapp.domain.telegram.logger.LoggerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLoggerRepository(): LoggerRepository = InMemoryLogger()

    @Provides
    @Singleton
    fun provideTelegramInstanceApi(): TelegramInstanceApi {
        val TOKEN = BuildConfig.TELEGRAM_API_TOKEN
        val BASE_URL = "https://api.telegram.org/bot$TOKEN/"

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TelegramInstanceApi::class.java)
    }
}