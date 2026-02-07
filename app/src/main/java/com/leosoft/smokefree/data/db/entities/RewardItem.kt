package com.leosoft.smokefree.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reward_items")
data class RewardItem(
    @PrimaryKey val id: String,
    val title: String,
    val price: Double,
    val iconName: String,
    val createdAt: Long
)
