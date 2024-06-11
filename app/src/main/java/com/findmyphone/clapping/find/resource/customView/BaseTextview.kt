package com.findmyphone.clapping.find.resource.customView

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.findmyphone.clapping.find.R

class BaseTextview @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    init {
//        val array = context.obtainStyledAttributes(attrs, R.styleable.BaseTextView)
        try {

        } finally {
//            array.recycle()
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}