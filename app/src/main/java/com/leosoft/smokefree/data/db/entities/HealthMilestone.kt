package com.leosoft.smokefree.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_milestones")
data class HealthMilestone(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val triggerDurationHours: Int,
    val iconName: String
)
