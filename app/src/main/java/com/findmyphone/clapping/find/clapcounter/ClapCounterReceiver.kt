package com.findmyphone.clapping.find.clapcounter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.findmyphone.clapping.find.BuildConfig

class ClapCounterReceiver : BroadcastReceiver() {

    companion object {
        const val CLAP_COUNTER_ACTION = BuildConfig.APPLICATION_ID + "CLAP_COUNTER_ACTION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getIntExtra(CLAP_COUNTER_ACTION, 0)
        val serviceIntent = Intent(context, ClapCounterService::class.java)
        serviceIntent.putExtra(CLAP_COUNTER_ACTION, action)
        context.startService(serviceIntent)
    }
}