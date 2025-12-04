package com.alexitodev.estacionamientoapp.data.telegram

import com.google.gson.annotations.SerializedName

data class TelegramUpdatesResponse(
    @SerializedName("ok")
    val ok: Boolean,
    @SerializedName("result")
    val result: List<TelegramUpdate>
)

data class TelegramUpdate(
    @SerializedName("message")
    val message: TelegramMessage?,
    @SerializedName("update_id")
    val updateId: Int
)

data class TelegramMessage(
    @SerializedName("chat")
    val chat: UpdateChat,
    @SerializedName("date")
    val date: Int,
    @SerializedName("entities")
    val entities: List<UpdateEntity>,
    @SerializedName("from")
    val from: UpdateFrom,
    @SerializedName("message_id")
    val messageId: Int,
    @SerializedName("text")
    val text: String
)

data class UpdateFrom(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("is_bot")
    val isBot: Boolean,
    @SerializedName("language_code")
    val languageCode: String,
    @SerializedName("last_name")
    val lastName: String
)

data class UpdateChat(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("type")
    val type: String
)

data class UpdateEntity(
    @SerializedName("length")
    val length: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("type")
    val type: String
)