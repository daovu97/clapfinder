package com.findmyphone.clapping.find.view.chargingalarm

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.base.BaseFragment
import com.findmyphone.clapping.find.data.local.Settings
import com.findmyphone.clapping.find.databinding.FragmentChargingAlarmBinding
import com.findmyphone.clapping.find.resource.customView.setOnImageRightClick
import com.findmyphone.clapping.find.resource.utils.getAdmobIfNeed
import com.findmyphone.clapping.find.resource.utils.setOnSingleClickListener
import com.findmyphone.clapping.find.view.main.HomeActivity
import com.findmyphone.clapping.find.view.sound.ManagerSoundActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.callback.NativeCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChargingAlarmFragment :
    BaseFragment<FragmentChargingAlarmBinding>(FragmentChargingAlarmBinding::inflate) {

    override fun initView() {
        super.initView()
        changeImageState(Settings.START_UNPLUGGING_ALARM.get(false))
        changeButton(Settings.START_UNPLUGGING_ALARM.get(false))
        binding.btnStart.setOnSingleClickListener {
            Settings.START_UNPLUGGING_ALARM.put(!Settings.START_UNPLUGGING_ALARM.get(false))
            (mActivity as? HomeActivity)?.registerChargerState(
                Settings.START_UNPLUGGING_ALARM.get(
                    false
                )
            )
            changeImageState(Settings.START_UNPLUGGING_ALARM.get(false))
            changeButton(Settings.START_UNPLUGGING_ALARM.get(false))
            binding.txtActive.text =
                getString(if (Settings.START_UNPLUGGING_ALARM.get(false)) R.string.tap_under_to_activate_charing_stop else R.string.tap_under_to_activate_charing)

        }

        binding.header.setOnImageRightClick {
            startActivity(Intent(mActivity, ManagerSoundActivity::class.java))
        }
        loadNativeHomeAds()
    }

    private fun changeImageState(isStart: Boolean = false) {
        binding.imageView.setImageResource(if (isStart) R.drawable.img_plugin else R.drawable.ic_tap_1)
    }

    private fun changeButton(isStart: Boolean = false) {
        binding.btnStart.text = getString(if (isStart) R.string.stop_tt else R.string.start_tt)
        binding.btnStart.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (isStart) R.drawable.bg_stop_button else R.drawable.bg_start_button
            )
        )
    }

    private fun loadNativeHomeAds() {
        fun hideAds() {
            binding.frmNative.visibility = View.INVISIBLE
            binding.frmNative.removeAllViews()
        }
        mActivity?.getAdmobIfNeed()
            ?.loadNativeAd(mActivity, getString(R.string.native_home), object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                    val adView = LayoutInflater.from(mActivity)
                        .inflate(R.layout.layout_native_small, null) as NativeAdView
                    binding.frmNative.removeAllViews()
                    binding.frmNative.addView(adView)
                    mActivity?.getAdmobIfNeed()?.pushAdsToViewCustom(nativeAd, adView)
                }

                override fun onAdFailedToLoad() {
                    hideAds()
                }
            }) ?: hideAds()
    }
}