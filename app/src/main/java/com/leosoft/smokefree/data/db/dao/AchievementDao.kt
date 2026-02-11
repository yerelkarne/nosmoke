package com.leosoft.smokefree.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.leosoft.smokefree.data.db.entities.AchievementDefinition
import com.leosoft.smokefree.data.db.entities.AchievementProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement_definitions")
    fun observeDefinitions(): Flow<List<AchievementDefinition>>

    @Query("SELECT * FROM achievement_progress")
    fun observeProgress(): Flow<List<AchievementProgress>>

    @Query("SELECT * FROM achievement_definitions")
    suspend fun getDefinitions(): List<AchievementDefinition>

    @Query("SELECT * FROM achievement_progress")
    suspend fun getProgress(): List<AchievementProgress>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefinitions(definitions: List<AchievementDefinition>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: List<AchievementProgress>)

    @Update
    suspend fun updateProgress(progress: List<AchievementProgress>)
}
