package com.leosoft.smokefree.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.concurrent.atomic.AtomicBoolean

class AppOpenAdManager(private val application: Application) : DefaultLifecycleObserver,
    Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private val isShowingAd = AtomicBoolean(false)
    private val adUnitId = "ca-app-pub-3940256099942544/9257395921"

    fun registerLifecycle() {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        loadAd()
    }

    override fun onStart(owner: LifecycleOwner) {
        showAdIfAvailable()
    }

    private fun loadAd() {
        if (appOpenAd != null) return
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            application,
            adUnitId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d("AppOpenAd", "Load failed: ${error.message}")
                }

                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                }
            }
        )
    }

    private fun showAdIfAvailable() {
        val activity = currentActivity ?: return
        if (isShowingAd.get() || appOpenAd == null) return

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isShowingAd.set(false)
                appOpenAd = null
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                isShowingAd.set(false)
                appOpenAd = null
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd.set(true)
            }
        }

        appOpenAd?.show(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
}
