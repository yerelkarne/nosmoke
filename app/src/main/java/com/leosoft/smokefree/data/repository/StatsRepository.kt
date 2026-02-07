package com.leosoft.smokefree.data.repository

import android.content.Context
import com.leosoft.smokefree.data.SmokeFreeStats
import com.leosoft.smokefree.data.StatsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class StatsRepository(context: Context) {
    private val dataStore = StatsDataStore(context)

    val statsFlow: Flow<DashboardStats> = dataStore.statsFlow.map { stats ->
        val now = System.currentTimeMillis()
        val elapsedMillis = if (stats.startTimestamp == 0L) 0L else (now - stats.startTimestamp).coerceAtLeast(0L)
        val smokeFreeDays = TimeUnit.MILLISECONDS.toDays(elapsedMillis).toInt()
        val notSmokedCount = (smokeFreeDays * stats.cigarettesPerDay).toLong()
        val packsSaved = notSmokedCount.toDouble() / stats.packSize.toDouble()
        val savedMoney = packsSaved * stats.packPrice
        val lifeMinutes = notSmokedCount * 11
        val lifeDays = (lifeMinutes / 1440).toInt()

        DashboardStats(
            smokeFreeDays = smokeFreeDays,
            notSmokedCount = notSmokedCount,
            savedMoney = savedMoney,
            lifeDays = lifeDays,
            elapsedMillis = elapsedMillis,
            stats = stats
        )
    }

    suspend fun ensureStartTimestamp() {
        dataStore.ensureStartTimestamp(System.currentTimeMillis())
    }

    suspend fun updateLastAchievementNotify(timestamp: Long) {
        dataStore.updateLastAchievementNotify(timestamp)
    }

    suspend fun updateLastHealthNotify(timestamp: Long) {
        dataStore.updateLastHealthNotify(timestamp)
    }

    suspend fun updateLastRewardNotify(timestamp: Long) {
        dataStore.updateLastRewardNotify(timestamp)
    }

    suspend fun updateLastStreakNotify(timestamp: Long) {
        dataStore.updateLastStreakNotify(timestamp)
    }

    suspend fun updateOnboardingCompleted(isCompleted: Boolean) {
        dataStore.updateOnboardingCompleted(isCompleted)
    }
}

data class DashboardStats(
    val smokeFreeDays: Int,
    val notSmokedCount: Long,
    val savedMoney: Double,
    val lifeDays: Int,
    val elapsedMillis: Long,
    val stats: SmokeFreeStats
)
