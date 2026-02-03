package com.leosoft.smokefree

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.leosoft.smokefree.ads.AppOpenAdManager
import com.leosoft.smokefree.notifications.NotificationHelper

class SmokeFreeApp : Application() {
    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        MobileAds.initialize(this)
        appOpenAdManager = AppOpenAdManager(this)
        appOpenAdManager.registerLifecycle()
    }
}
