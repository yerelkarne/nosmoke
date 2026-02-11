package com.leosoft.smokefree.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.statsDataStore: DataStore<Preferences> by preferencesDataStore(name = "stats")

class StatsDataStore(private val context: Context) {
    private val startTimestampKey = longPreferencesKey("start_timestamp")
    private val cigarettesPerDayKey = intPreferencesKey("cigarettes_per_day")
    private val packPriceKey = intPreferencesKey("pack_price")
    private val packSizeKey = intPreferencesKey("pack_size")
    private val onboardingKey = booleanPreferencesKey("onboarding_completed")
    private val premiumKey = booleanPreferencesKey("premium_purchased")
    private val lastAchievementNotifyKey = longPreferencesKey("last_achievement_notify")
    private val lastHealthNotifyKey = longPreferencesKey("last_health_notify")
    private val lastRewardNotifyKey = longPreferencesKey("last_reward_notify")
    private val lastStreakNotifyKey = longPreferencesKey("last_streak_notify")

    val statsFlow: Flow<SmokeFreeStats> = context.statsDataStore.data.map { prefs ->
        SmokeFreeStats(
            startTimestamp = prefs[startTimestampKey] ?: 0L,
            cigarettesPerDay = prefs[cigarettesPerDayKey] ?: 20,
            packPrice = prefs[packPriceKey] ?: 600,
            packSize = prefs[packSizeKey] ?: 20,
            onboardingCompleted = prefs[onboardingKey] ?: false,
            premiumPurchased = prefs[premiumKey] ?: false,
            lastAchievementNotify = prefs[lastAchievementNotifyKey] ?: 0L,
            lastHealthNotify = prefs[lastHealthNotifyKey] ?: 0L,
            lastRewardNotify = prefs[lastRewardNotifyKey] ?: 0L,
            lastStreakNotify = prefs[lastStreakNotifyKey] ?: 0L
        )
    }

    suspend fun ensureStartTimestamp(now: Long) {
        context.statsDataStore.edit { prefs ->
            if ((prefs[startTimestampKey] ?: 0L) == 0L) {
                prefs[startTimestampKey] = now
            }
        }
    }

    suspend fun updateLastAchievementNotify(timestamp: Long) {
        context.statsDataStore.edit { prefs ->
            prefs[lastAchievementNotifyKey] = timestamp
        }
    }

    suspend fun updateLastHealthNotify(timestamp: Long) {
        context.statsDataStore.edit { prefs ->
            prefs[lastHealthNotifyKey] = timestamp
        }
    }

    suspend fun updateLastRewardNotify(timestamp: Long) {
        context.statsDataStore.edit { prefs ->
            prefs[lastRewardNotifyKey] = timestamp
        }
    }

    suspend fun updateLastStreakNotify(timestamp: Long) {
        context.statsDataStore.edit { prefs ->
            prefs[lastStreakNotifyKey] = timestamp
        }
    }

    suspend fun updateOnboardingCompleted(isCompleted: Boolean) {
        context.statsDataStore.edit { prefs ->
            prefs[onboardingKey] = isCompleted
        }
    }
}

data class SmokeFreeStats(
    val startTimestamp: Long,
    val cigarettesPerDay: Int,
    val packPrice: Int,
    val packSize: Int,
    val onboardingCompleted: Boolean,
    val premiumPurchased: Boolean,
    val lastAchievementNotify: Long,
    val lastHealthNotify: Long,
    val lastRewardNotify: Long,
    val lastStreakNotify: Long
)
