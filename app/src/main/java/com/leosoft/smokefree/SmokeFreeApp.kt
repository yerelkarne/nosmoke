package com.leosoft.smokefree

import android.app.Application
import com.leosoft.smokefree.data.seed.SeedDataLoader
import com.leosoft.smokefree.notifications.NotificationHelper
import com.leosoft.smokefree.work.WorkScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmokeFreeApp : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            NotificationHelper.createChannel(this@SmokeFreeApp)

            val database = AppContainer.provideDatabase(this@SmokeFreeApp)
            val seedLoader = SeedDataLoader(this@SmokeFreeApp)
            val achievementRepo = AppContainer.achievementRepository(this@SmokeFreeApp)
            val healthRepo = AppContainer.healthRepository(this@SmokeFreeApp)
            val statsRepository = AppContainer.statsRepository(this@SmokeFreeApp)

            statsRepository.ensureStartTimestamp()
            achievementRepo.upsertDefinitions(seedLoader.loadAchievementDefinitions())
            if (database.healthMilestoneDao().getMilestones().isEmpty()) {
                healthRepo.insertMilestones(seedLoader.loadHealthMilestones())
            }
            AppContainer.achievementEngine(this@SmokeFreeApp).evaluate()

            WorkScheduler.scheduleNotifications(this@SmokeFreeApp)
        }
    }
}
