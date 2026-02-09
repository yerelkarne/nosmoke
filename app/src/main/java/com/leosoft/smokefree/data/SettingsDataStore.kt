package com.leosoft.smokefree.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    private val dailyCountKey = intPreferencesKey("daily_count")
    private val startMinutesKey = intPreferencesKey("start_minutes")
    private val endMinutesKey = intPreferencesKey("end_minutes")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    private val lastScheduledDayKey = longPreferencesKey("last_scheduled_day")
    private val favoritesKey = stringSetPreferencesKey("favorites")

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            dailyCount = prefs[dailyCountKey] ?: 5,
            startMinutes = prefs[startMinutesKey] ?: 8 * 60,
            endMinutes = prefs[endMinutesKey] ?: 20 * 60,
            notificationsEnabled = prefs[notificationsEnabledKey] ?: true,
            lastScheduledDay = prefs[lastScheduledDayKey] ?: 0L,
            favorites = prefs[favoritesKey] ?: emptySet()
        )
    }

    suspend fun updateSettings(dailyCount: Int, startMinutes: Int, endMinutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[dailyCountKey] = dailyCount
            prefs[startMinutesKey] = startMinutes
            prefs[endMinutesKey] = endMinutes
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[notificationsEnabledKey] = enabled
        }
    }

    suspend fun updateLastScheduledDay(epochDay: Long) {
        context.dataStore.edit { prefs ->
            prefs[lastScheduledDayKey] = epochDay
        }
    }

    suspend fun toggleFavorite(messageId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[favoritesKey] ?: emptySet()
            val id = messageId.toString()
            prefs[favoritesKey] = if (current.contains(id)) {
                current - id
            } else {
                current + id
            }
        }
    }
}

data class UserSettings(
    val dailyCount: Int,
    val startMinutes: Int,
    val endMinutes: Int,
    val notificationsEnabled: Boolean,
    val lastScheduledDay: Long,
    val favorites: Set<String>
)
