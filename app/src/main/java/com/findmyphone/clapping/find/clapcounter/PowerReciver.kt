package com.findmyphone.clapping.find.clapcounter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class PowerReciver : BroadcastReceiver() {

    private var lastChanging = false

    override fun onReceive(context: Context, intent: Intent) {
        val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        if (!isCharging && lastChanging) {
            val serviceIntent = Intent(context, ChargingAlarmService::class.java)
            context.startService(serviceIntent)
        }

        lastChanging = isCharging
    }
}