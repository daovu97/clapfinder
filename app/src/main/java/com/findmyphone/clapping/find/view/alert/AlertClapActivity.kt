package com.findmyphone.clapping.find.view.alert

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.findmyphone.clapping.find.application.base.BaseActivity
import com.findmyphone.clapping.find.clapcounter.ClapCounterAction
import com.findmyphone.clapping.find.clapcounter.ClapCounterReceiver
import com.findmyphone.clapping.find.clapcounter.ClapCounterService
import com.findmyphone.clapping.find.databinding.ActivityAlertBinding
import com.findmyphone.clapping.find.resource.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlertClapActivity : BaseActivity<ActivityAlertBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff()
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        binding.button.setOnSingleClickListener {
            val intent = Intent(this, ClapCounterService::class.java)
            intent.putExtra(ClapCounterReceiver.CLAP_COUNTER_ACTION, ClapCounterAction.STOP.value)
            startForegroundService(intent)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(ClapCounterService.notificationId)
            finish()
        }
    }

    override fun makeBinding(layoutInflater: LayoutInflater): ActivityAlertBinding {
        return ActivityAlertBinding.inflate(layoutInflater)
    }
}

fun Activity.turnScreenOnAndKeyguardOff() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )
    }
}

