package com.leosoft.smokefree.ads

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.height
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val adUnitIdValue = "ca-app-pub-3940256099942544/6300978111"
    val requestedAdSize = AdSize.LARGE_BANNER
    var bannerHeightDp by remember { mutableIntStateOf(requestedAdSize.height) }
    val density = LocalDensity.current

    AndroidView(
        modifier = modifier.height(with(density) { bannerHeightDp.toDp() }),
        factory = { ctx ->
            AdView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setAdSize(requestedAdSize)
                setAdUnitId(adUnitIdValue)
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        bannerHeightDp = adSize?.height ?: requestedAdSize.height
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
