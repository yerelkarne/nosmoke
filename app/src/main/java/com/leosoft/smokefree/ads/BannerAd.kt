package com.leosoft.smokefree.ads

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val adUnitIdValue = "ca-app-pub-3940256099942544/6300978111"
    var bannerHeightDp by remember { mutableStateOf(0.dp) }

    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.LARGE_BANNER)
            setAdUnitId(adUnitIdValue)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    post {
                        val loadedHeightPx = measuredHeight
                            .takeIf { it > 0 }
                            ?: adSize?.getHeightInPixels(context)
                            ?: 0
                        if (loadedHeightPx > 0) {
                            val density = context.resources.displayMetrics.density
                            bannerHeightDp = (loadedHeightPx / density).dp
                        }
                    }
                }

                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    bannerHeightDp = 0.dp
                }
            }
        }
    }

    DisposableEffect(adView) {
        adView.loadAd(AdRequest.Builder().build())
        onDispose {
            adView.destroy()
        }
    }

    AndroidView(
        modifier = modifier.height(bannerHeightDp),
        factory = { adView }
    )
}
