package com.findmyphone.clapping.find.resource.utils


import android.view.View

abstract class OnSingleClickListener : View.OnClickListener {

    private var lastClickTime: Long = 0
    abstract fun onSingleClick(v: View?)
    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime <= 900) {
            return
        }
        lastClickTime = currentTime
        onSingleClick(v)
    }
}

fun View.setOnSingleClickListener(onSingleClicks: (v: View?) -> Unit) {
    setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(v: View?) {
            onSingleClicks(v)
        }
    })
}