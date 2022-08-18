package com.sensibol.lucidmusic.singstr.gui.test

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.activity.BaseActivity
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.ActivityTestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class TestActivity : BaseActivity() {
    override val layoutResId: Int get() = R.layout.activity_test
    override val bindingInflater: (LayoutInflater) -> ViewBinding get() = ActivityTestBinding::inflate
}