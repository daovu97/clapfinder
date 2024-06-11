package com.findmyphone.clapping.find.resource.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

fun checkNetWork(context: Context?): Boolean? {
    if (context == null) {
        return false
    }
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capability =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capability != null) {
            if (capability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            return true
        }
    }
    return false
}