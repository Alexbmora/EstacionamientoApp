package com.alexitodev.estacionamientoapp.main.domain.telegram.logger

import android.annotation.SuppressLint

enum class LogSource {
    SYSTEM,
    TELEGRAM,
    BLUETOOTH,
    SERVER
}

enum class LogLevel {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

@SuppressLint("NewApi")
data class LogEntry (
    val message: String,
    val source: LogSource,
    val level: LogLevel,
    val timestamp: Long
)