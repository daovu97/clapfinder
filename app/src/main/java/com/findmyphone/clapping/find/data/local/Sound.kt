package com.findmyphone.clapping.find.data.local

import android.content.Context
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.findmyphone.clapping.find.R

data class CSound(val name: String, val url: String)
data class Sound(
    @StringRes val nameSound: Int, @RawRes val sound: Int? = null, val custom: CSound? = null
) {
    companion object {
        val default = Sound(R.string.sound_alert, R.raw.sound_alert)
    }
}

fun Sound.isCustom(): Boolean = sound == null && nameSound == R.string.custom_sound

fun Sound.uri(context: Context): String =
    if (sound != null) "android.resource://${context.packageName}/${sound}"
    else custom?.url ?: Sound.default.uri(context)