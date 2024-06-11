package com.findmyphone.clapping.find.resource.utils

import com.findmyphone.clapping.find.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class FBConfig private constructor() {

    private val remoteConfig = Firebase.remoteConfig
    fun config() {
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
        })

        remoteConfig.setDefaultsAsync(
            mapOf(
                show_ads to false,
                show_ads_inter to false
            )
        )

        remoteConfig.fetchAndActivate()
    }

    fun isShowAds(): Boolean {
        return remoteConfig.getBoolean(show_ads)
    }

    fun isShowAdsInter(): Boolean {
        return isShowAds() && remoteConfig.getBoolean(show_ads_inter)
    }

    companion object {
        val shared = FBConfig()
        const val show_ads = "show_ads"
        const val show_ads_inter = "show_ads_inter"
    }
}

