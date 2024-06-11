package com.findmyphone.clapping.find.view.main.tab

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.databinding.TabBarItemLayoutBinding
import com.findmyphone.clapping.find.resource.utils.gone
import com.findmyphone.clapping.find.resource.utils.visible

class TabBarItemView : ConstraintLayout {

    var text: String? = null
        set(value) {
            binding.tabText.text = value
            field = value
        }

    @DrawableRes
    var icon: Int? = null

    @ColorRes
    private var defaultColor: Int = R.color.color_default_tab1

    @ColorRes
    private var selectedColor: Int = R.color.white

    var isSelectedTab: Boolean = false
        set(value) {
            if (value) binding.view.visible() else binding.view.gone()
            binding.imageIcon.setColorFilter(
                ContextCompat.getColor(
                    context,
                    if (value) selectedColor else defaultColor
                )
            )
            if (value) binding.tabText.visible() else binding.tabText.gone()
            binding.tabText.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (value) selectedColor else defaultColor
                )
            )
            field = value
        }

    constructor(context: Context) : super(context) {
        initView(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs, 0)
    }

    constructor(
        context: Context, attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr)
    }

    lateinit var binding: TabBarItemLayoutBinding

    private fun initView(attrs: AttributeSet?, @AttrRes defStyleAttr: Int) {
        binding = TabBarItemLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        val customAttributesStyle =
            context.obtainStyledAttributes(attrs, R.styleable.TabBarItemView, defStyleAttr, 0)

        try {
            icon = customAttributesStyle.getResourceId(
                R.styleable.TabBarItemView_tab_icon,
                R.drawable.hands_clap_1
            )
            icon?.let { binding.imageIcon.setImageResource(it) }
            text =
                customAttributesStyle.getString(R.styleable.TabBarItemView_tab_text)
            isSelected =
                customAttributesStyle.getBoolean(R.styleable.TabBarItemView_tab_selected, false)
        } finally {
            customAttributesStyle.recycle()
        }
    }

}