package com.leosoft.smokefree.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement_progress")
data class AchievementProgress(
    @PrimaryKey val achievementId: String,
    val isUnlocked: Boolean,
    val progressValue: Long,
    val unlockedAt: Long?
)
