package com.findmyphone.clapping.find.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.base.BaseActivity
import com.findmyphone.clapping.find.data.local.Settings
import com.findmyphone.clapping.find.databinding.ActivitySplashBinding
import com.findmyphone.clapping.find.resource.utils.delay
import com.findmyphone.clapping.find.resource.utils.getAdmobIfNeed
import com.findmyphone.clapping.find.resource.utils.getAdmobInterIfNeed
import com.findmyphone.clapping.find.view.main.HomeActivity
import com.findmyphone.clapping.find.view.settings.LanguageActivity
import com.google.android.gms.ads.LoadAdError
import com.nlbn.ads.callback.InterCallback
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private var interCallback: InterCallback? = null
    override fun makeBinding(layoutInflater: LayoutInflater): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        interCallback = object : InterCallback() {
            override fun onAdClosed() {
                super.onAdClosed()
                checkScreen()
            }

            override fun onAdFailedToLoad(i: LoadAdError?) {
                super.onAdFailedToLoad(i)
                onAdClosed()
            }
        }

        fun delayCheckScreen() {
            delay(1000L) {
                checkScreen()
            }
        }

        getAdmobInterIfNeed()?.loadSplashInterAds2(
            this@SplashActivity,
            getString(R.string.inter_splash),
            3000,
            interCallback
        ) ?: delayCheckScreen()
    }

    override fun onResume() {
        super.onResume()
        getAdmobInterIfNeed()?.onCheckShowSplashWhenFail(this, interCallback, 1000)
    }

    companion object {
        private const val times = 100
    }

    override fun onDestroy() {
        super.onDestroy()
        getAdmobInterIfNeed()?.dismissLoadingDialog()
    }

    override fun onStop() {
        super.onStop()
        getAdmobInterIfNeed()?.dismissLoadingDialog()
    }

    private fun checkScreen() {
        if (!Settings.PASS_TUTORIAL.get(false)) {
            val intent = Intent(this@SplashActivity, LanguageActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@SplashActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}