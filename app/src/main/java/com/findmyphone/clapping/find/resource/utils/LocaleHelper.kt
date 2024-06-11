package com.findmyphone.clapping.find.resource.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*


object LocaleHelper {
    fun setLocale(context: Context, languageCode: String?, recreate: Boolean = true) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        if (recreate) (context as Activity).recreate()
    }
}

class MyContextWrapper(base: Context?) : ContextWrapper(base) {
    companion object {
        fun wrap(context: Context?, language: String): ContextWrapper {
            var context = context
            val config = context?.resources?.configuration
            var sysLocale: Locale? = null
            sysLocale = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                config?.let { getSystemLocale(it) }
            } else {
                config?.let { getSystemLocaleLegacy(it) }
            }
            if (language != "" && sysLocale?.language != language) {
                val locale = Locale(language)
                Locale.setDefault(locale)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    config?.let { setSystemLocale(it, locale) }
                } else {
                    config?.let { setSystemLocaleLegacy(it, locale) }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context = config?.let { context?.createConfigurationContext(it) }
            } else {
                context?.resources?.updateConfiguration(config, context.resources.displayMetrics)
            }
            return MyContextWrapper(context)
        }

        private fun getSystemLocaleLegacy(config: Configuration): Locale {
            return config.locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun getSystemLocale(config: Configuration): Locale {
            return config.locales[0]
        }

        private fun setSystemLocaleLegacy(config: Configuration, locale: Locale?) {
            config.locale = locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun setSystemLocale(config: Configuration, locale: Locale?) {
            config.setLocale(locale)
        }
    }
}