package com.alexitodev.estacionamientoapp.di

import com.alexitodev.estacionamientoapp.data.telegram.TelegramRepositoryImpl
import com.alexitodev.estacionamientoapp.domain.telegram.TelegramRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTelegramRepository(impl: TelegramRepositoryImpl): TelegramRepository
}