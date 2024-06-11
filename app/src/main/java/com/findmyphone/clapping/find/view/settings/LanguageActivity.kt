package com.findmyphone.clapping.find.view.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.Language
import com.findmyphone.clapping.find.application.base.BaseActivity
import com.findmyphone.clapping.find.databinding.FragmentLanguageBinding
import com.findmyphone.clapping.find.resource.customView.ActionType
import com.findmyphone.clapping.find.resource.customView.setOnBackClick
import com.findmyphone.clapping.find.resource.customView.setOnImageRightClick
import com.findmyphone.clapping.find.resource.utils.LocaleHelper
import com.findmyphone.clapping.find.resource.utils.getAdmobIfNeed
import com.findmyphone.clapping.find.view.main.HomeActivity
import com.findmyphone.clapping.find.view.permission.PermissionActivity
import com.findmyphone.clapping.find.view.settings.adapter.LanguageAdapter
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.callback.NativeCallback
import java.util.Locale

class LanguageActivity : BaseActivity<FragmentLanguageBinding>() {

    companion object {
        const val IS_FROM_SETTINGS = "IS_FROM_SETTINGS"
    }

    lateinit var adapter: LanguageAdapter
    override fun makeBinding(layoutInflater: LayoutInflater): FragmentLanguageBinding {
        return FragmentLanguageBinding.inflate(layoutInflater)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        val isFromSetting = intent?.extras?.getBoolean(IS_FROM_SETTINGS) == true
        if (!isFromSetting) {
            binding.headerLanguage.imageLeftDrawable = null
            binding.headerLanguage.actionLeftType = ActionType.NONE
        }
        binding.toolbar.targetElevation = 0F
        binding.recLanguages.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val allValue = Language.getAll()
        adapter = LanguageAdapter(context = this, allValue) { it, i ->
            adapter.setSelected(it.id)
        }

        binding.recLanguages.adapter = adapter

        adapter.setSelected(Language.current.id)

        val selected = Language.getAll().indexOfFirst { it.id == Language.current.id }
        if (selected >= 0) {
            binding.recLanguages.scrollToPosition(selected)
        }

        binding.headerLanguage.setOnBackClick {
            finish()
        }

        binding.headerLanguage.setOnImageRightClick {
            if (!isFromSetting) {
                Language.setCurrent(adapter.selectedID)
                this.let {
                    LocaleHelper.setLocale(it, Language.current.localizeCode, false)
                    val intent = Intent(it, PermissionActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Language.setCurrent(adapter.selectedID)
                LocaleHelper.setLocale(this, Language.current.localizeCode, false)
                this.let {
                    val intent = Intent(it, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
                    finish()
                }
            }
        }
        loadNativeLanguage()
    }

    private fun loadNativeLanguage() {
        try {
            getAdmobIfNeed()?.loadNativeAd(
                this,
                this.getString(R.string.native_language),
                object : NativeCallback() {
                    @SuppressLint("InflateParams")
                    override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                        val adView = LayoutInflater.from(this@LanguageActivity)
                            .inflate(R.layout.layout_native_language, null) as NativeAdView
                        binding.frAds.removeAllViews()
                        binding.frAds.addView(adView)
                        getAdmobIfNeed()?.pushAdsToViewCustom(nativeAd, adView)

                        val selected = Language.getAll().indexOfFirst { it.id == Language.current.id }
                        if (selected >= 0) {
                            binding.recLanguages.scrollToPosition(selected)
                        }
                    }

                    override fun onAdFailedToLoad() {
                        try {
                            getAdmobIfNeed()?.loadNativeAd(
                                this@LanguageActivity,
                                getString(R.string.native_language2),
                                object : NativeCallback() {
                                    @SuppressLint("InflateParams")
                                    override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                                        val adView =
                                            LayoutInflater.from(this@LanguageActivity)
                                                .inflate(
                                                    R.layout.layout_native_language,
                                                    null
                                                ) as NativeAdView
                                        binding.frAds.removeAllViews()
                                        binding.frAds.addView(adView)
                                        getAdmobIfNeed()?.pushAdsToViewCustom(nativeAd, adView)
                                    }

                                    override fun onAdFailedToLoad() {
                                        binding.frAds.removeAllViews()
                                    }

                                }) ?: binding.frAds.removeAllViews()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding.frAds.removeAllViews()
                        }
                    }

                }) ?: binding.frAds.removeAllViews()
        } catch (e: Exception) {
            e.printStackTrace()
            binding.frAds.removeAllViews()
        }
    }
}