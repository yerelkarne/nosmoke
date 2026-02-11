package com.leosoft.smokefree.data

import android.content.Context
import android.os.Build
import kotlinx.serialization.json.Json
import java.util.Locale

object MessageRepository {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadMessages(context: Context): List<Message> {
        val locale = getCurrentLocale(context)
        val candidates = buildAssetCandidates(locale)
        for (assetName in candidates) {
            val messages = runCatching { loadFromAsset(context, assetName) }.getOrNull()
            if (!messages.isNullOrEmpty()) {
                return messages
            }
        }
        return emptyList()
    }

    fun findMessage(context: Context, messageId: Int): Message? {
        val localized = loadMessages(context).firstOrNull { it.id == messageId }
        if (localized != null) return localized

        return runCatching { loadFromAsset(context, "messages_tr.json") }
            .getOrDefault(emptyList())
            .firstOrNull { it.id == messageId }
    }

    private fun loadFromAsset(context: Context, assetName: String): List<Message> {
        context.assets.open(assetName).use { inputStream ->
            val text = inputStream.bufferedReader().readText()
            return json.decodeFromString(text)
        }
    }

    private fun getCurrentLocale(context: Context): Locale {
        val configuration = context.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            configuration.locale
        }
    }

    private fun buildAssetCandidates(locale: Locale): List<String> {
        val language = locale.language.lowercase(Locale.ROOT)
        val region = locale.country.lowercase(Locale.ROOT)
        val candidates = mutableListOf<String>()

        if (region.isNotBlank()) {
            candidates.add("messages_${language}_$region.json")
        }
        candidates.add("messages_${language}.json")
        candidates.add("messages_en.json")
        candidates.add("messages_tr.json")
        return candidates.distinct()
    }
}
