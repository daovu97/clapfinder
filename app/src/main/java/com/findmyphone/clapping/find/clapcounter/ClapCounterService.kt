package com.findmyphone.clapping.find.clapcounter

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.MainApplication
import com.findmyphone.clapping.find.data.local.Settings
import com.findmyphone.clapping.find.resource.utils.hasPermissions
import com.findmyphone.clapping.find.view.alert.AlertClapActivity
import com.findmyphone.clapping.find.view.main.Duration
import com.findmyphone.clapping.find.view.main.HomeActivity
import com.findmyphone.clapping.find.view.sound.ManagerAudio
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ClapCounterService : Service() {

    private var clapDetector: ClapDetector? = null
    private var bufferSize = 1024

    companion object {
        const val serviceId = 11
        const val notificationId = 21
    }

    private fun startListener() {
        if (hasPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO))) {
            clapDetector?.cancel()
            clapDetector?.startDetectClap(
                sensitivity = Settings.SENSITIVITY.get(45).toDouble(),
                action = {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (!isPlaySound) {
                            startSound()
                            val notificationManager =
                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.notify(notificationId, buildNotification())
                        }
                    }
                },
                bufferSize = bufferSize,
                onError = {
                    bufferSize *= 2
                    startListener()
                })
        } else {
            stopSelf()
        }
    }

    private fun stopListener() {
        clapDetector?.cancel()
    }

    private fun startNotification() {
        val notification = createNotification()
        startForeground(serviceId, notification)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, HomeActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, MainApplication.CLAP_COUNTER_CHANNEL_ID)
                .setContentTitle(MainApplication.CONTEXT.getString(R.string.app_name))
                .setContentText(
                    MainApplication.CONTEXT.getString(R.string.service_running)
                )
                .setSmallIcon(R.drawable.hands_clap_1)
                .setSilent(true)
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .clearActions()
                .addAction(createActionClose())

        return builder.build()
    }

    private fun buildNotification(): Notification {
        val fullScreenIntent = Intent(this, AlertClapActivity::class.java)
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
                .setFullScreenIntent(fullScreenPendingIntent, true)
        notificationBuilder.setAutoCancel(true)
        return notificationBuilder.build()
    }

    override fun onCreate() {
        super.onCreate()
        MainApplication.isServiceRunning.postValue(true)
        clapDetector = ClapDetector()
        startListener()
        startNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getIntExtra(ClapCounterReceiver.CLAP_COUNTER_ACTION, 0)
        when (ClapCounterAction.getValue(action)) {
            ClapCounterAction.CLOSE -> stopSelf()
            ClapCounterAction.STOP -> stopSound()
            ClapCounterAction.RESET -> startListener()
            null -> {

            }
        }
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun createActionClose() = NotificationCompat.Action.Builder(
        R.drawable.baseline_close_24,
        getString(R.string.stop_lbl),
        getPendingIntent(this, ClapCounterAction.CLOSE)
    ).build()

    override fun onDestroy() {
        super.onDestroy()
        MainApplication.isServiceRunning.postValue(false)
        stopSound()
        stopListener()
    }

    private fun getPendingIntent(
        context: Context,
        clapCounterAction: ClapCounterAction
    ): PendingIntent? {
        val intent = Intent(this, ClapCounterReceiver::class.java)
        intent.putExtra(ClapCounterReceiver.CLAP_COUNTER_ACTION, clapCounterAction.value)
        return PendingIntent.getBroadcast(
            context,
            clapCounterAction.value,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private var vibrator: Vibrator? = null
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
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
            if (Settings.VIBRATION.get(true)) {
                vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager =
                        getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    getSystemService(VIBRATOR_SERVICE) as Vibrator
                }

                countDownTimer = object : CountDownTimer(Duration.current.duration, 5000) {
                    override fun onTick(p0: Long) {
                        vibrator?.cancel()
                        vibrator?.vibrate(
                            VibrationEffect.createOneShot(
                                5000,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    }

                    override fun onFinish() {
                        vibrator?.cancel()
                    }
                }
                countDownTimer?.start()
            }

            if (Settings.FLASH_LIGHT.get(true)) {
                cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                try {
                    val cameraIds = cameraManager?.cameraIdList
                    if (!cameraIds.isNullOrEmpty()) {

                        for (id in cameraIds) {
                            val characteristics = cameraManager?.getCameraCharacteristics(id)
                            val flashAvailable =
                                characteristics?.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                            val lensFacing = characteristics?.get(CameraCharacteristics.LENS_FACING)
                            if (flashAvailable == true && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                                cameraId = id
                                break
                            }
                        }
                        if (cameraId != null) {
                            cameraManager?.setTorchMode(cameraId!!, true)
                        }
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

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
        vibrator?.cancel()
        if (cameraId != null) {
            try {
                cameraManager?.setTorchMode(cameraId!!, false)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        val mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val volume = originalVolume ?: mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        mAudioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            volume,
            0
        )
    }

}

enum class ClapCounterAction(val value: Int) {
    STOP(2),
    CLOSE(3),
    RESET(4);

    companion object {
        fun getValue(int: Int?): ClapCounterAction? {
            return values().firstOrNull { it.value == int }
        }
    }

}
