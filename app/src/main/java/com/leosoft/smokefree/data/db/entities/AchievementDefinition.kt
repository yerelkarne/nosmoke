package com.leosoft.smokefree.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement_definitions")
data class AchievementDefinition(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val targetValue: Long,
    val metricType: String,
    val iconName: String,
    val isPremium: Boolean
)
