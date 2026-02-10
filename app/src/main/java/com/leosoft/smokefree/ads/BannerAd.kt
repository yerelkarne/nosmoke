package com.leosoft.smokefree.ads

import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val adUnitIdValue = "ca-app-pub-3940256099942544/6300978111"
    val requestedAdSize = AdSize.LARGE_BANNER
    val density = LocalDensity.current
    var bannerHeightDp by remember { mutableStateOf(requestedAdSize.height.dp) }

    AndroidView(
        modifier = modifier.height(bannerHeightDp),
        factory = { context ->
            AdView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setBackgroundColor(Color.TRANSPARENT)
                setAdSize(requestedAdSize)
                setAdUnitId(adUnitIdValue)
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        val loadedHeightPx = adSize?.getHeightInPixels(context)
                            ?: height
                        if (loadedHeightPx > 0) {
                            bannerHeightDp = with(density) { loadedHeightPx.toDp() }
                        }
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
