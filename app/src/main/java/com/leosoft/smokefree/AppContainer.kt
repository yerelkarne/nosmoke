package com.leosoft.smokefree

import android.content.Context
import androidx.room.Room
import com.leosoft.smokefree.data.db.AppDatabase
import com.leosoft.smokefree.data.repository.AchievementRepository
import com.leosoft.smokefree.data.repository.HealthRepository
import com.leosoft.smokefree.data.repository.RewardRepository
import com.leosoft.smokefree.data.repository.StatsRepository
import com.leosoft.smokefree.domain.AchievementEngine

object AppContainer {
    @Volatile
    private var database: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "smokefree.db"
            ).build().also { database = it }
        }
    }

    fun achievementRepository(context: Context): AchievementRepository {
        return AchievementRepository(provideDatabase(context).achievementDao())
    }

    fun rewardRepository(context: Context): RewardRepository {
        return RewardRepository(provideDatabase(context).rewardDao())
    }

    fun healthRepository(context: Context): HealthRepository {
        return HealthRepository(provideDatabase(context).healthMilestoneDao())
    }

    fun statsRepository(context: Context): StatsRepository {
        return StatsRepository(context)
    }

    fun achievementEngine(context: Context): AchievementEngine {
        return AchievementEngine(achievementRepository(context), statsRepository(context))
    }
}
