package com.alexitodev.estacionamientoapp.di

import com.alexitodev.estacionamientoapp.data.telegram.TelegramRepository
import com.alexitodev.estacionamientoapp.domain.telegram.ITelegramRepository
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
    abstract fun bindTelegramRepository(impl: TelegramRepository): ITelegramRepository
}