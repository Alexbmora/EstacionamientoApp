package com.alexitodev.estacionamientoapp.data.telegram

import com.google.gson.annotations.SerializedName

data class TelegramSendReponse(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("result")
    val result: SendMessageResult?
)

data class SendMessageResult(
    @SerializedName("chat")
    val chat: Chat,
    @SerializedName("date")
    val date: Int,
    @SerializedName("from")
    val from: From,
    @SerializedName("message_id")
    val messageId: Int,
    @SerializedName("text")
    val text: String
)

data class From(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("is_bot")
    val isBot: Boolean,
    @SerializedName("username")
    val username: String
)

data class Chat(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("type")
    val type: String
)
