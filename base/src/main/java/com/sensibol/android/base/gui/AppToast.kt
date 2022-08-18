package com.sensibol.android.base.gui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

object AppToast {

    private var toast: Toast? = null

    @SuppressLint("ShowToast")
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        if (toast == null) {
            toast = Toast.makeText(context.applicationContext, message, duration)
        } else {
            toast?.apply {
                setText(message)
                setDuration(duration)
                show()
            }
        }
        toast?.show()
    }
}