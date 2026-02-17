package com.leosoft.smokefree.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds
import java.util.concurrent.atomic.AtomicBoolean

object AdsInitializer {
    private val initialized = AtomicBoolean(false)

    fun initialize(context: Context) {
        if (initialized.compareAndSet(false, true)) {
            MobileAds.initialize(context)
        }
    }
}
