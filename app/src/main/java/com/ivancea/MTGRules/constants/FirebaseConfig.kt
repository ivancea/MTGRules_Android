package com.ivancea.MTGRules.constants

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig

object FirebaseConfig {
    private const val ADS_ACTIVE_BY_DEFAULT = "ads_active_by_default"

    fun getAdsActiveByDefault(): Boolean {
        return Firebase.remoteConfig.getBoolean(ADS_ACTIVE_BY_DEFAULT)
    }

    fun getBannerAdUnitId(): String {
        return Firebase.remoteConfig.getString("banner_ad_unit_id").ifEmpty {
            "<the-default-banner-ad-unit-id>"
        }
    }
}