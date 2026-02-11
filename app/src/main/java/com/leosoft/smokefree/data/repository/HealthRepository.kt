package com.leosoft.smokefree.data.repository

import com.leosoft.smokefree.data.db.dao.HealthMilestoneDao
import com.leosoft.smokefree.data.db.entities.HealthMilestone
import kotlinx.coroutines.flow.Flow

class HealthRepository(private val dao: HealthMilestoneDao) {
    fun observeMilestones(): Flow<List<HealthMilestone>> = dao.observeMilestones()

    suspend fun getMilestones(): List<HealthMilestone> = dao.getMilestones()

    suspend fun insertMilestones(items: List<HealthMilestone>) {
        dao.insertMilestones(items)
    }
}
