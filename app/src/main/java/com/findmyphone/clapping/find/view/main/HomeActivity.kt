package com.findmyphone.clapping.find.view.main

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.MainApplication
import com.findmyphone.clapping.find.application.base.BaseActivity
import com.findmyphone.clapping.find.clapcounter.ChargingAlarmService
import com.findmyphone.clapping.find.clapcounter.ClapCounterAction
import com.findmyphone.clapping.find.clapcounter.ClapCounterReceiver
import com.findmyphone.clapping.find.clapcounter.ClapCounterService
import com.findmyphone.clapping.find.clapcounter.PowerReciver
import com.findmyphone.clapping.find.data.local.Settings
import com.findmyphone.clapping.find.databinding.ActivityMainTabbarBinding
import com.findmyphone.clapping.find.resource.utils.ConstantsYakin
import com.findmyphone.clapping.find.resource.utils.compatColor
import com.findmyphone.clapping.find.view.chargingalarm.ChargingAlarmFragment
import com.findmyphone.clapping.find.view.settings.SettingFragment
import com.nlbn.ads.util.AppOpenManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityMainTabbarBinding>() {
    override fun makeBinding(layoutInflater: LayoutInflater): ActivityMainTabbarBinding {
        return ActivityMainTabbarBinding.inflate(layoutInflater)
    }

    fun stopService() {
        val intent = Intent(this, ClapCounterService::class.java)
        intent.putExtra(ClapCounterReceiver.CLAP_COUNTER_ACTION, ClapCounterAction.CLOSE.value)
        startForegroundService(intent)
    }

    fun startService() {
        val intent = Intent(this, ClapCounterService::class.java)
        startForegroundService(intent)
    }

    private val powerReciver: PowerReciver by lazy {
        PowerReciver()
    }

    fun registerChargerState(isEnabled: Boolean) {
        try {
            if (isEnabled) {
                IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        this.registerReceiver(powerReciver, ifilter, RECEIVER_NOT_EXPORTED)
                    } else {
                        this.registerReceiver(powerReciver, ifilter)
                    }
                }
            } else {
                stopService(Intent(this, ChargingAlarmService::class.java))
                this.unregisterReceiver(powerReciver)
            }
        } catch (e: Exception) {
            // already unregistered
        }
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        registerChargerState(
            Settings.START_UNPLUGGING_ALARM.get(
                false
            )
        )
        initViewPager()
    }

    private fun initViewPager() {
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> HomeFragment()
                    1 -> ChargingAlarmFragment()
                    else -> SettingFragment()
                }
            }

            override fun getItemCount(): Int {
                return 3
            }
        }
        binding.tabBarView.setOnTabChangeListener {
            (application as? MainApplication)?.currentSelected?.postValue(it)
        }
        (application as? MainApplication)?.currentSelected?.observe(this) {
            binding.tabBarView.currentSelectedIndex = it
            binding.viewPager.setCurrentItem(it, false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (ConstantsYakin.checkResume) {
            AppOpenManager.getInstance().enableAppResume()
        }
    }
}


enum class Duration {
    D15S, D30S, D1M, D2M;

    val duration: Long
        get() {
            return when (this) {
                D15S -> 15000L
                D30S -> 30000L
                D1M -> 60000L
                D2M -> 2 * 60000L
            }
        }

    companion object {
        fun get(position: Int) = Duration.values().getOrNull(position)

        fun index(duration: Duration): Int = Duration.values().indexOf(duration)
        var current: Duration
            get() {
                return get(Settings.TIME_SOUND.get(-1)) ?: D30S
            }
            set(value) {
                Settings.TIME_SOUND.put(Duration.values().indexOf(value))
            }
    }


}

fun Switch.changeColorTrack(context: Context) {
    trackTintList = if (isChecked) {

        val trackColorStateList = ColorStateList.valueOf(context.compatColor(R.color.color_green2))
        trackColorStateList
    } else {

        val trackColorStateList = ColorStateList.valueOf(context.compatColor(R.color.color_brown))
        trackColorStateList
    }
}



