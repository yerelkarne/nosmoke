package com.leosoft.smokefree

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.leosoft.smokefree.ads.AppOpenAdManager
import com.leosoft.smokefree.data.seed.SeedDataLoader
import com.leosoft.smokefree.work.WorkScheduler
import com.leosoft.smokefree.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmokeFreeApp : Application() {
    private lateinit var appOpenAdManager: AppOpenAdManager
    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        MobileAds.initialize(this)
        appOpenAdManager = AppOpenAdManager(this)
        appOpenAdManager.registerLifecycle()

        applicationScope.launch {
            val database = AppContainer.provideDatabase(this@SmokeFreeApp)
            val seedLoader = SeedDataLoader(this@SmokeFreeApp)
            val achievementRepo = AppContainer.achievementRepository(this@SmokeFreeApp)
            val healthRepo = AppContainer.healthRepository(this@SmokeFreeApp)
            val statsRepository = AppContainer.statsRepository(this@SmokeFreeApp)

            statsRepository.ensureStartTimestamp()
            if (database.achievementDao().getDefinitions().isEmpty()) {
                achievementRepo.upsertDefinitions(seedLoader.loadAchievementDefinitions())
            }
            if (database.healthMilestoneDao().getMilestones().isEmpty()) {
                healthRepo.insertMilestones(seedLoader.loadHealthMilestones())
            }
            AppContainer.achievementEngine(this@SmokeFreeApp).evaluate()
        }

        WorkScheduler.scheduleNotifications(this)
    }
}
