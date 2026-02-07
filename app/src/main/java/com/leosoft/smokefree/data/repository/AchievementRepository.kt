package com.leosoft.smokefree.data.repository

import com.leosoft.smokefree.data.db.dao.AchievementDao
import com.leosoft.smokefree.data.db.entities.AchievementDefinition
import com.leosoft.smokefree.data.db.entities.AchievementProgress
import kotlinx.coroutines.flow.Flow

class AchievementRepository(private val dao: AchievementDao) {
    fun observeDefinitions(): Flow<List<AchievementDefinition>> = dao.observeDefinitions()

    fun observeProgress(): Flow<List<AchievementProgress>> = dao.observeProgress()

    suspend fun getDefinitions(): List<AchievementDefinition> = dao.getDefinitions()

    suspend fun getProgress(): List<AchievementProgress> = dao.getProgress()

    suspend fun upsertDefinitions(items: List<AchievementDefinition>) {
        dao.insertDefinitions(items)
    }

    suspend fun upsertProgress(items: List<AchievementProgress>) {
        dao.insertProgress(items)
    }

    suspend fun updateProgress(items: List<AchievementProgress>) {
        dao.updateProgress(items)
    }
}
