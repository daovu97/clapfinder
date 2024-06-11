package com.findmyphone.clapping.find.resource.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.findmyphone.clapping.find.application.base.MyActivity
import com.nlbn.ads.util.Admob
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Context.getAdmobIfNeed(): Admob? {
    return if (checkNetWork(this) == true) {
        return if (FBConfig.shared.isShowAds()) Admob.getInstance()
        else null
    } else null
}

fun Context.getAdmobInterIfNeed(): Admob? {
    return if (checkNetWork(this) == true) {
        return if (FBConfig.shared.isShowAdsInter()) Admob.getInstance()
        else null
    } else null
}

object Constant {
    val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels

    val screenHeight: Int = Resources.getSystem().displayMetrics.heightPixels
}

fun Context.compatColor(int: Int): Int {
    return ContextCompat.getColor(this, int)
}

fun Context.compactDrawable(int: Int): Drawable? {
    return ContextCompat.getDrawable(this, int)
}

object Converter {
    fun dpFromPx(px: Int): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    fun pixelsToSp(pixels: Int): Float {
        val scaledDensity = Resources.getSystem().displayMetrics.scaledDensity
        return pixels / scaledDensity
    }

    fun asPixels(value: Int): Int {
        val scale = Resources.getSystem().displayMetrics.density
        val dpAsPixels = (value * scale + 0.5f)
        return dpAsPixels.toInt()
    }
}

fun MyActivity.delay(timeMillis: Long, completion: () -> Unit) {
    activityScope.launch {
        kotlinx.coroutines.delay(timeMillis)
        completion.invoke()
    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
    this.isEnabled = true
}

fun View.hidden() {
    this.visibility = View.INVISIBLE
    this.isEnabled = false
}

fun View.gone() {
    this.visibility = View.GONE
    this.isEnabled = false
}


fun Context?.convertDpToPixel(dp: Float): Float {
    this ?: return 0f
    return dp * (resources
        .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.string(pattern: String = "yyyy-MM-dd HH:mm"): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(formatter)
}

fun Context?.hasPermissions(permissions: Array<String>): Boolean = permissions.all {
    this ?: return@all false
    ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

fun View.asPixels(value: Int): Int {
    val scale = resources.displayMetrics.density
    val dpAsPixels = (value * scale + 0.5f)
    return dpAsPixels.toInt()
}