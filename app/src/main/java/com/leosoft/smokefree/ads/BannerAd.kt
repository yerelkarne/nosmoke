package com.leosoft.smokefree.ads

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val adUnitIdValue = "ca-app-pub-3940256099942544/6300978111"
    val adSize = AdSize.LARGE_BANNER

    AndroidView(
        modifier = modifier.height(adSize.height.dp),
        factory = {
            AdView(context).apply {
                setAdSize(adSize)
                setAdUnitId(adUnitIdValue)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
