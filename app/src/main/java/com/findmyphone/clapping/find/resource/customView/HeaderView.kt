package com.findmyphone.clapping.find.resource.customView

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.databinding.CustomHeaderviewBinding
import com.findmyphone.clapping.find.resource.utils.Converter
import com.findmyphone.clapping.find.resource.utils.gone
import com.findmyphone.clapping.find.resource.utils.setOnSingleClickListener
import com.findmyphone.clapping.find.resource.utils.visible

interface CustomHeader {
    val actionLeftView: View?
    val actionRightView: View?
}

fun CustomHeader.setOnBackClick(onclick: () -> Unit) {
    actionLeftView?.setOnSingleClickListener { onclick.invoke() }
}

fun CustomHeader.setOnImageRightClick(onclick: () -> Unit) {
    actionRightView?.setOnSingleClickListener { onclick.invoke() }
}

enum class ActionType(val value: Int) {
    NONE(0), IMAGE(1), TEXT(2);

    companion object {
        fun get(value: Int): ActionType = ActionType.values().find { it.value == value } ?: NONE
    }
}

enum class TitleGravity(val value: Int) {
    CENTER(0), LEFT(1);

    companion object {
        fun get(value: Int): TitleGravity =
            TitleGravity.values().find { it.value == value } ?: CENTER
    }
}

class HeaderView : FrameLayout, CustomHeader {
    @StringRes
    var title: Int = R.string.app_name
        set(value) {
            binding.txtCenter.text = context.getString(value)
            binding.txtTitle.text = context.getString(value)
            field = value
        }

    var titleString: String = ""
        set(value) {
            binding.txtCenter.text = value
            binding.txtTitle.text = value
            field = value
        }

    var titleSpannable: SpannableStringBuilder = SpannableStringBuilder()
        set(value) {
            binding.txtCenter.text = value
            binding.txtTitle.text = value
            field = value
        }

    var titleTextSize: Float = 16f
        set(value) {
            binding.txtCenter.textSize = value
            binding.txtTitle.textSize = value
            field = value
        }

    var actionRightType: ActionType = ActionType.NONE
        set(value) {
            when (value) {
                ActionType.NONE -> {
                    binding.imgRightAction.gone(); binding.txtRightAction.gone()
                }

                ActionType.IMAGE -> {
                    binding.imgRightAction.visible(); binding.txtRightAction.gone()
                }

                ActionType.TEXT -> {
                    binding.imgRightAction.gone(); binding.txtRightAction.visible()
                }
            }
            field = value
        }

    var actionLeftType: ActionType = ActionType.IMAGE
        set(value) {
            when (value) {
                ActionType.NONE -> {
                    binding.imgLeftAction.gone()
                    if (titleGravity == TitleGravity.LEFT) {
                        binding.headerTitleView.setPadding(
                            Converter.asPixels(16),
                            0,
                            binding.headerTitleView.paddingRight,
                            0
                        )
                    }
                }

                ActionType.IMAGE -> {
                    binding.imgLeftAction.visible()
                }

                ActionType.TEXT -> {
                    binding.imgLeftAction.gone()
                }
            }
            field = value
        }

    var titleGravity: TitleGravity = TitleGravity.CENTER
        set(value) {
            when (value) {
                TitleGravity.CENTER -> {
                    binding.txtCenter.visible()
                    binding.txtTitle.gone()
                    if (actionLeftType == ActionType.NONE && actionRightType == ActionType.NONE) {
                        binding.headerTitleView.setPadding(
                            Converter.asPixels(16),
                            0,
                            Converter.asPixels(16),
                            0
                        )
                    }
                }

                TitleGravity.LEFT -> {
                    binding.txtCenter.gone()
                    binding.txtTitle.visible()
                    if (actionLeftType == ActionType.NONE) {
                        binding.headerTitleView.setPadding(
                            Converter.asPixels(16),
                            0,
                            binding.headerTitleView.paddingRight,
                            0
                        )
                    }
                }
            }
            field = value
        }

    var imageRightDrawable: Drawable? = null
        set(value) {
            field = value
            value ?: return
            binding.imgRightAction.setImageDrawable(value)
        }

    var imageLeftDrawable: Drawable? = null
        set(value) {
            field = value
            value ?: return
            binding.imgLeftAction.setImageDrawable(value)
        }

    var actionRightTitle: String? = null
        set(value) {
            field = value
            value ?: return
            binding.txtRightAction.text = value
        }

    constructor(context: Context) : super(context) {
        initView(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs, 0)
    }

    constructor(
        context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr)
    }

    private lateinit var binding: CustomHeaderviewBinding

    private fun initView(attrs: AttributeSet?, @AttrRes defStyleAttr: Int) {
        binding = CustomHeaderviewBinding.inflate(LayoutInflater.from(context), this, true)

        val customHeader =
            context.obtainStyledAttributes(attrs, R.styleable.HeaderView, defStyleAttr, 0)

        try {
            imageRightDrawable = customHeader.getDrawable(R.styleable.HeaderView_src_right)
            imageLeftDrawable = customHeader.getDrawable(R.styleable.HeaderView_src_left)
                ?: ContextCompat.getDrawable(context, R.drawable.ic_back_black)
            actionRightType =
                ActionType.get(customHeader.getInt(R.styleable.HeaderView_right_action_type, 0))
            actionLeftType =
                ActionType.get(customHeader.getInt(R.styleable.HeaderView_left_action_type, 1))
            titleGravity =
                TitleGravity.get(customHeader.getInt(R.styleable.HeaderView_title_gravity, 0))
            titleTextSize = customHeader.getDimension(R.styleable.HeaderView_title_text_size, 20f)
            title = customHeader.getResourceId(R.styleable.HeaderView_cs_title, R.string.app_name)
            actionRightTitle = customHeader.getString(R.styleable.HeaderView_action_right_title)
        } finally {
            customHeader.recycle()
        }
    }

    override val actionLeftView: View? by lazy { binding.imgLeftAction }

    override val actionRightView: View? by lazy {
        return@lazy when (actionRightType) {
            ActionType.IMAGE -> binding.imgRightAction
            ActionType.TEXT -> binding.txtRightAction
            else -> null
        }
    }
}
