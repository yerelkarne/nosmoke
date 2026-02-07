package com.leosoft.smokefree.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.leosoft.smokefree.AppContainer
import com.leosoft.smokefree.notifications.NotificationHelper
import com.leosoft.smokefree.ui.NavigationRoutes
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class AchievementNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val achievementRepo = AppContainer.achievementRepository(applicationContext)
        val healthRepo = AppContainer.healthRepository(applicationContext)
        val rewardRepo = AppContainer.rewardRepository(applicationContext)
        val statsRepository = AppContainer.statsRepository(applicationContext)
        val dashboard = statsRepository.statsFlow.first()
        val progresses = achievementRepo.getProgress()
        val lastAchievementNotified = dashboard.stats.lastAchievementNotify
        val newestUnlock = progresses
            .filter { it.isUnlocked && (it.unlockedAt ?: 0L) > lastAchievementNotified }
            .maxByOrNull { it.unlockedAt ?: 0L }

        if (newestUnlock != null) {
            NotificationHelper.showSystemNotification(
                context = applicationContext,
                title = "Yeni Rozet Kazandın",
                message = "Yeni bir başarı kilidi açıldı. Detaylara göz at.",
                route = NavigationRoutes.Trophies.route
            )
            statsRepository.updateLastAchievementNotify(System.currentTimeMillis())
        }

        val startTimestamp = dashboard.stats.startTimestamp
        if (startTimestamp > 0L) {
            val milestones = healthRepo.getMilestones()
            val lastHealthNotify = dashboard.stats.lastHealthNotify
            val now = System.currentTimeMillis()
            val nextMilestone = milestones.firstOrNull { milestone ->
                val triggerAt = startTimestamp + TimeUnit.HOURS.toMillis(milestone.triggerDurationHours.toLong())
                triggerAt in (lastHealthNotify + 1)..now
            }
            if (nextMilestone != null) {
                NotificationHelper.showSystemNotification(
                    context = applicationContext,
                    title = "Sağlık Dönüm Noktası",
                    message = nextMilestone.description,
                    route = NavigationRoutes.Health.route
                )
                statsRepository.updateLastHealthNotify(now)
            }
        }

        val rewards = rewardRepo.observeRewards().first()
        val lastRewardNotify = dashboard.stats.lastRewardNotify
        val rewardReady = rewards.firstOrNull { reward -> dashboard.savedMoney >= reward.price && reward.createdAt > lastRewardNotify }
        if (rewardReady != null) {
            NotificationHelper.showSystemNotification(
                context = applicationContext,
                title = "Ödül Hedefi Tamamlandı",
                message = "${rewardReady.title} için hedefe ulaştın.",
                route = NavigationRoutes.Rewards.route
            )
            statsRepository.updateLastRewardNotify(System.currentTimeMillis())
        }

        val lastStreakNotify = dashboard.stats.lastStreakNotify
        val streakDue = System.currentTimeMillis() - lastStreakNotify > TimeUnit.HOURS.toMillis(24)
        if (streakDue) {
            NotificationHelper.showSystemNotification(
                context = applicationContext,
                title = "Sigarasız Seriyi Koru",
                message = "Bugün de hedefini koru, serin devam etsin!",
                route = NavigationRoutes.Progress.route
            )
            statsRepository.updateLastStreakNotify(System.currentTimeMillis())
        }
        return Result.success()
    }
}
