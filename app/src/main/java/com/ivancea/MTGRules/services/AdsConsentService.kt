package com.ivancea.MTGRules.services

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class AdsConsentService @Inject constructor() {

    private val adsInitializedFlag = AtomicBoolean(false);

    val adsInitialized = MutableStateFlow(true);

    fun gatherConsent(activity: Activity) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        val params = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { loadAndShowError ->
                    if (loadAndShowError != null) {
                        Log.w(
                            "AdBanner", String.format(
                                "Consent form dismissed, code %s: %s",
                                loadAndShowError.errorCode,
                                loadAndShowError.message
                            )
                        )
                    }

                    if (consentInformation.canRequestAds()) {
                        initializeAds(activity)
                    }
                }
            },
            { requestConsentError ->
                Log.w(
                    "AdBanner", String.format(
                        "Consent info update failed with code %s: %s",
                        requestConsentError.errorCode,
                        requestConsentError.message
                    )
                )
            },
        )

        if (consentInformation.canRequestAds()) {
            initializeAds(activity)
        }
    }

    private fun initializeAds(activity: Activity) {
        if (adsInitializedFlag.getAndSet(true)) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(activity) {}

            adsInitialized.value = true
        }
    }
}