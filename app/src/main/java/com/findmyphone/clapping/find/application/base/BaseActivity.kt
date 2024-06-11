package com.findmyphone.clapping.find.application.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.findmyphone.clapping.find.application.Language
import com.findmyphone.clapping.find.resource.utils.MyContextWrapper


typealias MyActivity = BaseActivity<*>

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {
    val activityScope: CoroutineLauncher by lazy {
        return@lazy CoroutineLauncher()
    }

    open val binding: B by lazy { makeBinding(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        hideNavigationBar()
        setupView(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, Language.current.localizeCode))
    }

    abstract fun makeBinding(layoutInflater: LayoutInflater): B

    open fun setupView(savedInstanceState: Bundle?) {}


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideNavigationBar()
        }
    }

    val permissionsResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value }) {
                onPermissionGranted(permissions)
            } else {
                onPermissionDenied(permissions)
            }
        }

    open fun onPermissionGranted(permissions: Map<String, Boolean>) {}

    open fun onPermissionDenied(permissions: Map<String, Boolean>) {}

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancelCoroutines()
    }
}

abstract class BaseVMActivity<B : ViewBinding, VM : BaseViewModel> : BaseActivity<B>() {
    abstract val viewModel: VM
}

fun AppCompatActivity.hideNavigationBar() {
    window.decorView.apply {
        systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
    WindowCompat.setDecorFitsSystemWindows(window, true)
    val controllerCompat = WindowInsetsControllerCompat(window, window.decorView)
    controllerCompat.hide(WindowInsetsCompat.Type.navigationBars())
    controllerCompat.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    controllerCompat.isAppearanceLightStatusBars = true
}

