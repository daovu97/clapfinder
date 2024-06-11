package com.findmyphone.clapping.find.view.settings.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.findmyphone.clapping.find.R
import com.findmyphone.clapping.find.application.Language
import com.findmyphone.clapping.find.databinding.ItemLanguageBinding
import com.findmyphone.clapping.find.resource.utils.compatColor

class LanguageAdapter(
    val context: Context,
    private var dataSet: List<Language>,
    var didSelect: ((Language, Int) -> Unit)? = null
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {
    var selectedID = -1

    @SuppressLint("NotifyDataSetChanged")
    fun setSelected(position: Int) {
        selectedID = position
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(language: Language) {
            binding.imgFlagLanguage.setImageResource(language.flag)
            binding.txtNameLanguage.text = context.getString(language.nameFlag)
            binding.relItemLanguage.setBackgroundResource(
                (if (language.id == selectedID) R.drawable.bg_select_language else R.drawable.bg_language) as Int
            )
            binding.txtNameLanguage.setTextColor(
                (if (language.id != selectedID) context.compatColor(R.color.txt_color) else context.compatColor(
                    R.color.white
                ))
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ).apply {
            binding.root.setOnClickListener {
                didSelect?.invoke(dataSet[adapterPosition], adapterPosition)
            }
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}