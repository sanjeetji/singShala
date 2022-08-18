package com.sensibol.android.base.gui.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.displayName
import timber.log.Timber

abstract class BaseActivity : FragmentActivity() {

    @get:LayoutRes
    protected abstract val layoutResId: Int

    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding

    private var _binding: ViewBinding? = null

    protected open val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.v("onCreate IN $displayName")
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
        Timber.v("onCreate OUT $displayName")
    }

    override fun onDestroy() {
        Timber.v("onDestroy IN $displayName")
        _binding = null
        super.onDestroy()
        Timber.v("onDestroy OUT $displayName")
    }
}