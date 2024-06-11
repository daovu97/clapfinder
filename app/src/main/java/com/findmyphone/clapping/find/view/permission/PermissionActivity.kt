package com.findmyphone.clapping.find.view.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Switch
import android.widget.TextView
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.base.BaseActivity
import com.findmyphone.clapping.find.data.local.Settings
import com.findmyphone.clapping.find.databinding.ActivityPermissionBinding
import com.findmyphone.clapping.find.resource.utils.compatColor
import com.findmyphone.clapping.find.resource.utils.getAdmobIfNeed
import com.findmyphone.clapping.find.resource.utils.gone
import com.findmyphone.clapping.find.resource.utils.hasPermissions
import com.findmyphone.clapping.find.resource.utils.setOnSingleClickListener
import com.findmyphone.clapping.find.view.main.HomeActivity
import com.findmyphone.clapping.find.view.main.changeColorTrack
import com.findmyphone.clapping.find.view.permission.PermissionActivity.Companion.audioPermission
import com.findmyphone.clapping.find.view.permission.PermissionActivity.Companion.postNotificationPermission
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.AppOpenManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {

    companion object {
        const val audioPermission = Manifest.permission.RECORD_AUDIO
        const val postNotificationPermission = Manifest.permission.POST_NOTIFICATIONS

    }

    override fun makeBinding(layoutInflater: LayoutInflater): ActivityPermissionBinding {
        return ActivityPermissionBinding.inflate(layoutInflater)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)

        (binding.header.actionRightView as? TextView)?.setEnable(false)

        (binding.header.actionRightView as? TextView)?.setOnSingleClickListener {
            Settings.PASS_TUTORIAL.put(true)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.switchRecordAudio.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked && view.isPressed) {
                binding.frAds.visibility = View.GONE
                requestRecordAudioPermission {
                    binding.switchRecordAudio.isChecked = false
                    goToSettingApp()
                }
            }

            if (!isChecked && view.isPressed) {
                binding.switchRecordAudio.isChecked = true
                goToSettingApp()
            }
            binding.switchRecordAudio.changeColorTrack(this)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            binding.switchPermission.setOnCheckedChangeListener { view, isChecked ->
                if (isChecked && view.isPressed) {
                    binding.frAds.visibility = View.GONE
                    requestNotificationPermission {
                        binding.switchPermission.isChecked = false
                        goToSettingApp()
                    }
                }

                if (!isChecked && view.isPressed) {
                    binding.switchPermission.isChecked = true
                    goToSettingApp()
                }
                binding.switchPermission.changeColorTrack(this)
            }
        } else {
            binding.containerPermission.gone()
        }
        loadNative()

    }


    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(PermissionActivity::class.java)
        if (this.hasPermissions(arrayOf(audioPermission))) {
            onPermissionGranted(mapOf(audioPermission to true))
        } else {
            onPermissionDenied(mapOf(audioPermission to false))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (this.hasPermissions(arrayOf(postNotificationPermission))) {
                onPermissionGranted(mapOf(postNotificationPermission to true))
            } else {
                onPermissionDenied(mapOf(postNotificationPermission to false))
            }
        }
    }


    private fun Switch.setCheck(checked: Boolean) {
        isChecked = checked
//        isEnabled = !isChecked
    }

    override fun onPermissionGranted(permissions: Map<String, Boolean>) {
        super.onPermissionGranted(permissions)
        binding.frAds.visibility = View.VISIBLE
        if (permissions.containsKey(audioPermission)) {
            binding.switchRecordAudio.setCheck(true)
            setEnableNextButton(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && permissions.containsKey(
                postNotificationPermission
            )
        ) {
            binding.switchPermission.setCheck(true)
        }
        setEnableNextButton()
    }

    override fun onPermissionDenied(permissions: Map<String, Boolean>) {
        super.onPermissionDenied(permissions)
        binding.frAds.visibility = View.VISIBLE
        if (permissions.containsKey(audioPermission)) {
            binding.switchRecordAudio.setCheck(false)
            setEnableNextButton(false)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && permissions.containsKey(
                postNotificationPermission
            )
        ) {
            binding.switchPermission.setCheck(false)
        }
        setEnableNextButton()
    }

    private fun setEnableNextButton(isCheck: Boolean) {


    }

    private fun loadNative() {
        fun hideAds() {
            binding.frAds.removeAllViews()
            binding.frAds.visibility = View.GONE
        }
        try {
            getAdmobIfNeed()?.loadNativeAd(
                this,
                getString(R.string.native_permission),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        val adView = LayoutInflater.from(this@PermissionActivity)
                            .inflate(R.layout.layout_native_language, null) as NativeAdView
                        binding.frAds.removeAllViews()
                        binding.frAds.addView(adView)
                        getAdmobIfNeed()?.pushAdsToViewCustom(nativeAd, adView)
                    }

                    override fun onAdFailedToLoad() {
                        super.onAdFailedToLoad()
                        hideAds()
                    }
                }) ?: hideAds()
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
            hideAds()
        }
    }

    private fun TextView.setEnable(enable: Boolean = true) {
        setTextColor(if (enable) context.compatColor(R.color.primaryColor) else Color.GRAY)
        isEnabled = enable
    }

    private fun setEnableNextButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            (binding.header.actionRightView as? TextView)?.setEnable(binding.switchRecordAudio.isChecked && binding.switchPermission.isChecked)
        } else {
            (binding.header.actionRightView as? TextView)?.setEnable(binding.switchRecordAudio.isChecked)
        }
    }
}

fun BaseActivity<*>.requestRecordAudioPermission(action: () -> Unit) {
    if (!shouldShowRequestPermissionRationale(audioPermission)) {
        permissionsResult.launch(arrayOf(audioPermission))
    } else {
        action.invoke()
    }
}

fun BaseActivity<*>.requestNotificationPermission(action: () -> Unit) {
    if (!shouldShowRequestPermissionRationale(postNotificationPermission)) {
        permissionsResult.launch(arrayOf(postNotificationPermission))
    } else {
        action.invoke()
    }
}

fun Activity.goToSettingApp() {
    AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", this.packageName, null)
    intent.data = uri
    this.startActivity(intent)
}