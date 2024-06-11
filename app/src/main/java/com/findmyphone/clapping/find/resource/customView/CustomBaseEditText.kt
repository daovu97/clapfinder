package com.findmyphone.clapping.find.resource.customView


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Observer
import com.findmyphone.clapping.find.R

@SuppressLint("CustomViewStyleable")
class BaseEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.BaseEditText)
        try {

        } finally {
            array.recycle()
        }
    }

}



