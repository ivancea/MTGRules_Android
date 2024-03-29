package com.ivancea.MTGRules.presentation.main.components.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

@Composable
fun AdBanner(showAds: Boolean, bannerAdUnitId: String, onAdError: (adError: LoadAdError) -> Unit) {
    if (!showAds) {
        return
    }

    val width = LocalConfiguration.current.screenWidthDp

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            MobileAds.initialize(context) { }

            AdView(context).apply {
                val adSize =
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
                setAdSize(adSize)
                adUnitId = bannerAdUnitId
                adListener = object : com.google.android.gms.ads.AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        onAdError(error)
                    }
                }

                loadAd(AdRequest.Builder().build())
            }
        }
    )
}