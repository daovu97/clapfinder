package com.findmyphone.clapping.find.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.MainApplication
import com.findmyphone.clapping.find.application.base.BaseFragment
import com.findmyphone.clapping.find.data.local.Settings
import com.findmyphone.clapping.find.databinding.ActivityMainBinding
import com.findmyphone.clapping.find.resource.customView.setOnBackClick
import com.findmyphone.clapping.find.resource.customView.setOnImageRightClick
import com.findmyphone.clapping.find.resource.utils.convertDpToPixel
import com.findmyphone.clapping.find.resource.utils.getAdmobIfNeed
import com.findmyphone.clapping.find.resource.utils.hasPermissions
import com.findmyphone.clapping.find.resource.utils.setOnSingleClickListener
import com.findmyphone.clapping.find.view.permission.PermissionActivity
import com.findmyphone.clapping.find.view.settings.SettingFragment
import com.findmyphone.clapping.find.view.sound.ManagerSoundActivity
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.callback.NativeCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun initView() {
        super.initView()
        MainApplication.isServiceRunning.observe(this) {
            binding.imgBackgroundActive.isSelected = it
        }


        binding.apply {
            btnActive.setOnSingleClickListener {
                if (mActivity.hasPermissions(
                        arrayOf(PermissionActivity.audioPermission)
                    )
                ) {
                    changeActive()
                    Settings.PASS_TUTORIAL_HOME.put(true)
                } else {
                    startActivity(Intent(mActivity, PermissionActivity::class.java))
                }
            }

            setupActionButton(btnFlash,
                txtFlashLight,
                isOn = { Settings.FLASH_LIGHT.get(true) },
                valueChange = {
                    Settings.FLASH_LIGHT.put(it)
                })

            setupActionButton(btnVibra,
                txtVib,
                isOn = { Settings.VIBRATION.get(true) },
                valueChange = {
                    Settings.VIBRATION.put(it)
                })

            header.setOnImageRightClick {
                startActivity(Intent(mActivity, ManagerSoundActivity::class.java))
            }
        }
        loadNativeHomeAds()
        binding.root.clearFocus()
    }

    private fun setupActionButton(
        btn: CardView, textView: TextView, isOn: () -> Boolean, valueChange: (Boolean) -> Unit
    ) {

        fun change(isOn: Boolean) {
            mActivity?.let {
                btn.setCardBackgroundColor(
                    ContextCompat.getColor(
                        it, if (isOn) R.color.primaryColor else R.color.gray
                    )
                )
            }
            btn.cardElevation = if (isOn) mActivity.convertDpToPixel(12f) else 0f
            mActivity?.let {
                textView.setTextColor(
                    ContextCompat.getColor(
                        it, if (isOn) R.color.white else R.color.txt_color
                    )
                )
            }
        }
        change(isOn())
        btn.setOnClickListener {
            valueChange.invoke(!isOn())
            change(isOn())
        }
    }

    private fun changeActive() {
        val isActive = MainApplication.isServiceRunning.value ?: false
        binding.imgBackgroundActive.isSelected = !isActive
        if (isActive) {
            binding.txtActive.text = getString(R.string.tap_under_to_activate_clap_nfinder_mode)
            (mActivity as? HomeActivity)?.stopService()
        } else {
            binding.txtActive.text = getString(R.string.tapToDeActiveTitle)
            (mActivity as? HomeActivity)?.startService()
        }
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