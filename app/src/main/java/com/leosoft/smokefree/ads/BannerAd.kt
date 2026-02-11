package com.leosoft.smokefree.ads

import android.app.Activity
import android.util.DisplayMetrics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val adUnitIdValue = "ca-app-pub-3940256099942544/6300978111"

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(getAdaptiveAdSize(ctx))
                setAdUnitId(adUnitIdValue)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

private fun getAdaptiveAdSize(context: android.content.Context): AdSize {
    val metrics = DisplayMetrics()
    val activity = context as? Activity
    activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
    val density = metrics.density
    val adWidthPixels = metrics.widthPixels.toFloat()
    val adWidth = (adWidthPixels / density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
}
