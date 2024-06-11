package com.findmyphone.clapping.find.view.sound

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.base.BaseSingleAdapter
import com.findmyphone.clapping.find.application.base.BaseViewHolder
import com.findmyphone.clapping.find.data.local.CSound
import com.findmyphone.clapping.find.data.local.Settings
import com.findmyphone.clapping.find.data.local.Sound
import com.findmyphone.clapping.find.data.local.isCustom
import com.findmyphone.clapping.find.databinding.ItemSoundBinding
import com.findmyphone.clapping.find.resource.utils.compatColor

class SoundAdapter(
    private val context: Context,
    private val onEvent: (Sound, Int) -> Unit,
    private val onLongClick: (Sound, Int) -> Unit
) : BaseSingleAdapter<Sound, ItemSoundBinding>() {

    override fun createViewBinding(parent: ViewGroup, viewType: Int): ItemSoundBinding {
        return ItemSoundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    var selectedPosition: String = Settings.soundUri(context)

    init {
        setupData(listSound())
    }

    fun getCurrent(): Sound? = listSound().firstOrNull { selectedPosition == it.uri(context) }

    fun setSelect(uri: String) {
        if (uri != selectedPosition) {
            selectedPosition = uri
            notifyDataSetChanged()
        }
    }

    override fun bindingViewHolder(holder: BaseViewHolder<ItemSoundBinding>, position: Int) {
        val item = listItem.getOrNull(position)

        if (item?.isCustom() == true) {
            holder.binding.txtTitle.text = buildSpannedString {
                append(context.getString(R.string.custom_sound))
                append(" ")

                bold {
                    if (item.custom?.name != null) append(item.custom.name)
                    else color(context.compatColor(R.color.primaryColor)) {
                        append(
                            context.getString(R.string.custom_sound_chose)
                        )
                    }
                }

            }
        } else {
            holder.binding.txtTitle.text = item?.nameSound?.let { context.getString(it) }
        }
        holder.binding.relItemLanguage.setBackgroundResource(
            (if (selectedPosition == item?.uri(context)) R.drawable.bg_select_language else R.drawable.bg_language)
        )
        holder.binding.txtTitle.setTextColor(
            (if (selectedPosition != item?.uri(context)) context.compatColor(R.color.txt_color) else context.compatColor(
                R.color.white
            ))
        )
    }

    private fun Sound.uri(context: Context): String? =
        if (sound != null) "android.resource://${context.packageName}/${sound}"
        else custom?.url

    override fun createViewHolder(binding: ItemSoundBinding): BaseViewHolder<ItemSoundBinding> {
        return BaseViewHolder(binding).apply {

            binding.relItemLanguage.setOnClickListener { v ->
                listItem.getOrNull(absoluteAdapterPosition)?.let {
                    if (it.uri(context) != selectedPosition) {
                        onEvent.invoke(it, absoluteAdapterPosition)
                    }
                }
            }

            binding.relItemLanguage.setOnLongClickListener {
                listItem.getOrNull(absoluteAdapterPosition)?.let {
                    onLongClick.invoke(it, absoluteAdapterPosition)
                }
                return@setOnLongClickListener true
            }
        }
    }

    companion object {
        fun customSound(): CSound? {
            val a = Settings.CUSTOM_SOUND.get("").trim().split("|<>|")
            return if (a.size < 2) null
            else CSound(a[0], a[1])
        }

        fun listSound(): List<Sound> = listOf(
            Sound(R.string.custom_sound, null, customSound()),
            Sound.default,
            Sound(R.string.cat_meowing, R.raw.cat),
            Sound(R.string.dog_barking, R.raw.dog),
            Sound(R.string.party_horn, R.raw.boat_horn),
            Sound(R.string.laugh, R.raw.laugh),
            Sound(R.string.chicken, R.raw.chicken),
            Sound(R.string.police_whistle, R.raw.police),
            Sound(R.string.cavalry, R.raw.cavalry),
            Sound(R.string.door_bell, R.raw.doorbell),
            Sound(R.string.train_horn, R.raw.train_horn)
        )
    }
}
