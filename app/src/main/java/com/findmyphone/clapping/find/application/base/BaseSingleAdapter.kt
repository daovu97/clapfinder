package com.findmyphone.clapping.find.application.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.findmyphone.clapping.find.data.local.Sound
import com.findmyphone.clapping.find.databinding.ItemLoadmoreBinding

abstract class BaseSingleAdapter<E, B : ViewBinding> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listItem: MutableList<E?> = mutableListOf()

    private var isLoadMore = false

    fun loadMore() {
        if (isLoadMore) return
        isLoadMore = true
        listItem.add(null)
        notifyItemInserted(listItem.size - 1)
    }

    open fun setupData(list: List<E>) {
        listItem.clear()
        listItem.addAll(list)
        notifyDataSetChanged()
    }

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    fun getItemAt(position: Int): E? {
        return listItem[position]
    }

    fun addData(list: List<E>, index: Int) {

        listItem.addAll(index, list)
        notifyItemRangeChanged(index, list.size)
    }

    fun appendWhenLoadMore(list: List<E>) {
        if (isLoadMore) {
            listItem.removeLast(); isLoadMore = false
        }

        listItem.addAll(listItem.size - 1, list)
        notifyItemRangeChanged(listItem.size - 1, list.size)
    }

    override fun getItemViewType(position: Int): Int {
        return if (listItem[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    abstract fun createViewBinding(parent: ViewGroup, viewType: Int): B
    abstract fun createViewHolder(binding: B): BaseViewHolder<B>
    abstract fun bindingViewHolder(holder: BaseViewHolder<B>, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val binding = createViewBinding(parent, viewType)
                createViewHolder(binding = binding)
            }

            else -> {
                val binding =
                    ItemLoadmoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder !is LoadingViewHolder) {
            bindingViewHolder(holder as BaseViewHolder<B>, position)
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    class LoadingViewHolder(itemView: ItemLoadmoreBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }
}

class BaseViewHolder<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)
