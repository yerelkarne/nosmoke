package com.leosoft.smokefree.ads

import android.graphics.Color
import android.view.ViewGroup
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
    val requestedAdSize = AdSize.LARGE_BANNER
    var bannerHeightDp by remember { mutableStateOf(0.dp) }

    val adView = remember {
        val fixedHeightPx = requestedAdSize.getHeightInPixels(context)
        AdView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                fixedHeightPx
            )
            minimumHeight = 0
            setPadding(0, 0, 0, 0)
            setBackgroundColor(Color.TRANSPARENT)
            setAdSize(requestedAdSize)
            setAdUnitId(adUnitIdValue)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    bannerHeightDp = requestedAdSize.height.dp
                }

                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    bannerHeightDp = 0.dp
                }
            }
        }
    }

    DisposableEffect(adView) {
        adView.loadAd(AdRequest.Builder().build())
        onDispose { adView.destroy() }
    }

    AndroidView(
        modifier = modifier.height(bannerHeightDp),
        factory = { adView },
        update = { view ->
            view.layoutParams.height = requestedAdSize.getHeightInPixels(context)
            view.requestLayout()
        }
    )
}
