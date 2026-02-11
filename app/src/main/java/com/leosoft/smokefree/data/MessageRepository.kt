package com.leosoft.smokefree.data

import android.content.Context
import kotlinx.serialization.json.Json

object MessageRepository {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadMessages(context: Context): List<Message> {
        context.assets.open("messages_tr.json").use { inputStream ->
            val text = inputStream.bufferedReader().readText()
            return json.decodeFromString(text)
        }
    }

    fun findMessage(context: Context, messageId: Int): Message? {
        return loadMessages(context).firstOrNull { it.id == messageId }
    }
}
