package com.leosoft.smokefree.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.leosoft.smokefree.data.db.entities.HealthMilestone
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthMilestoneDao {
    @Query("SELECT * FROM health_milestones ORDER BY triggerDurationHours ASC")
    fun observeMilestones(): Flow<List<HealthMilestone>>

    @Query("SELECT * FROM health_milestones")
    suspend fun getMilestones(): List<HealthMilestone>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(items: List<HealthMilestone>)
}
