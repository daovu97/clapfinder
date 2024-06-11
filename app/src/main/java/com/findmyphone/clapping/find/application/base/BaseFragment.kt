package com.findmyphone.clapping.find.application.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias MyFragment = BaseFragment<*>

abstract class BaseFragment<B : ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> B) :
    Fragment() {
    private var _binding: B? = null

    val binding: B
        get() = _binding ?: error("Not found binding")
    val mActivity: MyActivity?
        get() = this.activity as? MyActivity

    private var view: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        initView()
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun reloadWhenLogin() {}

    open fun initView() {}
}

abstract class BaseVMFragment<B : ViewBinding, VM : BaseViewModel>(inflate: (LayoutInflater, ViewGroup?, Boolean) -> B) :
    BaseFragment<B>(inflate) {
    abstract val viewModel: VM
}