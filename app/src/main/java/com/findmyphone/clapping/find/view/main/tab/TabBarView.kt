package com.findmyphone.clapping.find.view.main.tab

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.findmyphone.clapping.find.databinding.TabBarLayoutBinding

class TabBarView : ConstraintLayout {

    var binding: TabBarLayoutBinding =
        TabBarLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    var currentSelectedIndex: Int = 0
        set(value) {
            listTabItem.forEachIndexed { index, tabBarItemView ->
                tabBarItemView.isSelectedTab = index == value
            }
            field = value
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context, attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    private var listTabItem = mutableListOf<TabBarItemView>()
    private var callback: ((Int) -> Unit)? = null

    init {
        listTabItem.add(binding.tab1)
        listTabItem.add(binding.tab2)
        listTabItem.add(binding.tab3)
        setupOnClick()
    }

    private fun setupOnClick() {
        listTabItem.forEachIndexed { index, tabBarItemView ->
            tabBarItemView.setOnClickListener {
                currentSelectedIndex = index
                callback?.invoke(index)
            }
        }
    }

    fun setOnTabChangeListener(callback: (Int) -> Unit) {
        this.callback = callback
    }

}

