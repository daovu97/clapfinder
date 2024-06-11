package com.findmyphone.clapping.find.view.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.findmyphone.clapping.find.BuildConfig
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.base.BaseFragment
import com.findmyphone.clapping.find.databinding.FragmentSettingBinding
import com.findmyphone.clapping.find.resource.utils.ConstantsYakin
import com.findmyphone.clapping.find.resource.utils.getAdmobIfNeed
import com.findmyphone.clapping.find.resource.utils.gone
import com.findmyphone.clapping.find.resource.utils.setOnSingleClickListener
import com.findmyphone.clapping.find.view.settings.LanguageActivity.Companion.IS_FROM_SETTINGS
import com.nlbn.ads.util.AppOpenManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    override fun initView() {
        super.initView()
        binding.btnLanguage.setOnSingleClickListener {
            val bundle = Bundle()
            bundle.putBoolean(IS_FROM_SETTINGS, true)
            val intent = Intent(mActivity, LanguageActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.btnRateUs.setOnSingleClickListener {
            ConstantsYakin.checkResume = true
            launchMarket()
        }

        binding.btnShare.setOnSingleClickListener {
            shareThisApp()
        }

        binding.btnPrivacyPolicy.setOnSingleClickListener {
            AppOpenManager.getInstance().disableAppResume()
            getUrlView(BuildConfig.URL_PRIVACY)
        }
        binding.btnFeedback.setOnClickListener {
            sendFeedback(
                BuildConfig.EMAIL_ADDRESS,
                getString(R.string.app_name) + "Support",
                "\n\n\n Version:" + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
            )
        }

        loadInterSetting()
    }

    private fun launchMarket() {
        mActivity?.apply {
            val uri = Uri.parse("market://details?id=$packageName")
            val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
            try {
                startActivity(myAppLinkToMarket)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Unable to find market app", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadInterSetting() {
        try {
            mActivity?.getAdmobIfNeed()?.loadBanner(mActivity, getString(R.string.banner_all))
                ?: binding.rlBannerList.gone()
        } catch (e: java.lang.Exception) {
            binding.rlBannerList.gone()
        }
    }

    private fun getUrlView(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(url)
        )
        startActivity(intent)
    }

    private fun shareThisApp() {
        AppOpenManager.getInstance().disableAppResume()
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Note")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        )
        startActivity(Intent.createChooser(shareIntent, "Share"))
    }

    private fun sendFeedback(toEmail: String, subject: String, text: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            //error message
        }
    }
}