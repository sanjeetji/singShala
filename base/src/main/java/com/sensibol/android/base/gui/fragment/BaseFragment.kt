package com.sensibol.android.base.gui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.displayName
import timber.log.Timber

abstract class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.v("onCreate IN $displayName")
        super.onCreate(savedInstanceState)
        onInitViewModel()
        Timber.v("onCreate OUT $displayName")
    }

    protected open fun onInitViewModel() {}

    @get:LayoutRes
    protected abstract val layoutResId: Int

    protected abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding

    private var _binding: ViewBinding? = null

    protected open val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView IN $displayName")
        _binding = bindingInflater(inflater, container, false)
        Timber.v("onCreateView OUT $displayName")
        return binding.root
    }

    protected abstract fun onInitView()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated IN $displayName")
        super.onViewCreated(view, savedInstanceState)
        onInitView()
        Timber.v("onViewCreated OUT $displayName")
    }

    override fun onDestroyView() {
        Timber.v("onDestroyView IN $displayName")
        _binding = null
        super.onDestroyView()
        Timber.v("onDestroyView OUT $displayName")
    }

    override fun onDestroy() {
        Timber.v("onDestroy IN $displayName")
        super.onDestroy()
        Timber.v("onDestroy OUT $displayName")
    }
}