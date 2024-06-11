package com.findmyphone.clapping.find.application

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.findmyphone.clapping.find.BuildConfig
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.data.local.Settings
import com.findmyphone.clapping.find.resource.utils.FBConfig
import com.findmyphone.clapping.find.view.splash.SplashActivity
import com.nlbn.ads.util.AdsApplication
import com.nlbn.ads.util.AppOpenManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : AdsApplication() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var CONTEXT: Context
        const val CLAP_COUNTER_CHANNEL_ID = BuildConfig.APPLICATION_ID + "CLAP_COUNTER_CHANNEL"
        const val CLAP_COUNTER_CHANNEL_NAME = BuildConfig.APPLICATION_ID + "Clap counter channel"

        const val CLAP_NOTIFICATION_CHANNEL_ID =
            BuildConfig.APPLICATION_ID + "CLAP_NOTIFICATION_CHANNEL"
        const val CLAP_NOTIFICATION_CHANNEL_NAME =
            BuildConfig.APPLICATION_ID + "Clap notification channel"
        val isServiceRunning = MutableLiveData(false)
        val isServiceCharingRunning = MutableLiveData(false)
    }

    val currentSelected = MutableLiveData(0)

    override fun onCreate() {
        super.onCreate()
        CONTEXT = this
        FBConfig.shared.config()
        Settings.migrate()
        createChanel()

        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
    }

    override fun enableAdsResume(): Boolean {
        return FBConfig.shared.isShowAdsInter()
    }

    override fun enableAdjustTracking(): Boolean {
        return false
    }

    override fun getListTestDeviceId(): List<String?>? {
        return null
    }

    override fun getResumeAdId(): String {
        return getString(R.string.app_open_on_resume)
    }

    override fun getAdjustToken(): String {
        return ""
    }

    override fun buildDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    private fun createChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            val clapChannel = NotificationChannel(
                CLAP_COUNTER_CHANNEL_ID,
                CLAP_COUNTER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            clapChannel.setSound(null, null)
            notificationManager?.createNotificationChannel(clapChannel)

            val notificationChannel = NotificationChannel(
                CLAP_NOTIFICATION_CHANNEL_ID,
                CLAP_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }
}
