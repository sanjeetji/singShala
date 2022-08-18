package com.sensibol.lucidmusic.singstr.gui.login

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.activity.BaseActivity
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class LoginActivity : BaseActivity() {

    override val layoutResId: Int = R.layout.activity_login
    override val bindingInflater: (LayoutInflater) -> ViewBinding = ActivityLoginBinding::inflate
    override val binding get() : ActivityLoginBinding = super.binding as ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = this.let { ContextCompat.getColor(it, R.color.bg_page) }
    }
}