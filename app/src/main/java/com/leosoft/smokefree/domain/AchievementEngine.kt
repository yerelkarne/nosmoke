package com.leosoft.smokefree.domain

import com.leosoft.smokefree.data.db.entities.AchievementDefinition
import com.leosoft.smokefree.data.db.entities.AchievementProgress
import com.leosoft.smokefree.data.repository.AchievementRepository
import com.leosoft.smokefree.data.repository.DashboardStats
import com.leosoft.smokefree.data.repository.StatsRepository
import kotlinx.coroutines.flow.first

class AchievementEngine(
    private val achievementRepository: AchievementRepository,
    private val statsRepository: StatsRepository
) {
    suspend fun evaluate() {
        val definitions = achievementRepository.getDefinitions()
        if (definitions.isEmpty()) return
        val stats = statsRepository.statsFlow.first()
        val progressList = achievementRepository.getProgress().associateBy { it.achievementId }
        val now = System.currentTimeMillis()

        val updated = definitions.map { definition ->
            val current = progressList[definition.id]
            val progressValue = calculateProgress(definition, stats)
            val isUnlocked = progressValue >= definition.targetValue

            AchievementProgress(
                achievementId = definition.id,
                isUnlocked = isUnlocked,
                progressValue = progressValue,
                unlockedAt = if (isUnlocked) (current?.unlockedAt ?: now) else null
            )
        }

        achievementRepository.upsertProgress(updated)
    }

    private fun calculateProgress(definition: AchievementDefinition, stats: DashboardStats): Long {
        return when (definition.metricType) {
            MetricType.NOT_SMOKED_COUNT.name -> stats.notSmokedCount
            MetricType.SMOKE_FREE_DAYS.name -> stats.smokeFreeDays.toLong()
            MetricType.LIFE_GAINED_DAYS.name -> stats.lifeDays.toLong()
            MetricType.APP_EVENT.name -> resolveAppEvent(definition, stats)
            else -> 0L
        }
    }

    private fun resolveAppEvent(definition: AchievementDefinition, stats: DashboardStats): Long {
        return when (definition.id) {
            "onboarding_done" -> if (stats.stats.onboardingCompleted) 1L else 0L
            else -> 0L
        }
    }
}

enum class MetricType {
    NOT_SMOKED_COUNT,
    SMOKE_FREE_DAYS,
    LIFE_GAINED_DAYS,
    APP_EVENT
}
