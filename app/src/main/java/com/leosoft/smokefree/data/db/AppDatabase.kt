package com.leosoft.smokefree.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.leosoft.smokefree.data.db.dao.AchievementDao
import com.leosoft.smokefree.data.db.dao.HealthMilestoneDao
import com.leosoft.smokefree.data.db.dao.RewardDao
import com.leosoft.smokefree.data.db.entities.AchievementDefinition
import com.leosoft.smokefree.data.db.entities.AchievementProgress
import com.leosoft.smokefree.data.db.entities.HealthMilestone
import com.leosoft.smokefree.data.db.entities.RewardItem

@Database(
    entities = [
        AchievementDefinition::class,
        AchievementProgress::class,
        RewardItem::class,
        HealthMilestone::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun achievementDao(): AchievementDao
    abstract fun rewardDao(): RewardDao
    abstract fun healthMilestoneDao(): HealthMilestoneDao
}
