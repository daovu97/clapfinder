package com.findmyphone.clapping.find.clapcounter

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.MainApplication
import com.findmyphone.clapping.find.data.local.Settings
import com.findmyphone.clapping.find.view.main.Duration
import com.findmyphone.clapping.find.view.main.HomeActivity
import com.findmyphone.clapping.find.view.sound.ManagerAudio
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChargingAlarmService : Service() {

    companion object {
        const val serviceId = 12
        const val notificationId = 22
    }

    private fun startListener() {
        CoroutineScope(Dispatchers.Main).launch {
            if (!isPlaySound) {
                startSound()
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(notificationId, buildNotification())
            }
        }
    }

    private fun buildNotification(): Notification {
        val fullScreenIntent = Intent(this, HomeActivity::class.java)
        val fullScreenPendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, MainApplication.CLAP_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.hands_clap_1)
                .setContentTitle(MainApplication.CONTEXT.getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(fullScreenPendingIntent)
        notificationBuilder.setAutoCancel(true)
        return notificationBuilder.build()
    }

    override fun onCreate() {
        super.onCreate()
        MainApplication.isServiceCharingRunning.postValue(true)
        startListener()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startListener()
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        MainApplication.isServiceCharingRunning.postValue(false)
        stopSound()
    }

    private var handler: Handler? = null
    private var countDownTimer: CountDownTimer? = null
    private val mediaPlayer: ManagerAudio by lazy {
        ManagerAudio(MainApplication.CONTEXT)
    }
    private var originalVolume: Int? = null
    private var isPlaySound: Boolean = false

    private fun startSound() {
        isPlaySound = true
        val mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val volume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        originalVolume = volume

        mAudioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            Settings.VOLUME.get(volume),
            0
        )

        handler = Handler(Looper.getMainLooper())

        mediaPlayer.play(Settings.soundUri(this)) {
            handler?.postDelayed({
                stopSound()
            }, Duration.current.duration)
        }
    }

    private fun stopSound() {
        isPlaySound = false
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
        handler?.removeCallbacksAndMessages(null)
        mediaPlayer.release()
        countDownTimer?.cancel()

        val mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val volume = originalVolume ?: mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        mAudioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            volume,
            0
        )
    }

}

enum class CharingAlarmAction(val value: Int) {
    STOP(2),
    CLOSE(3),
    RESET(4);

    companion object {
        fun getValue(int: Int?): CharingAlarmAction? {
            return values().firstOrNull { it.value == int }
        }
    }

}
