package com.sensibol.lucidmusic.singstr.gui.app.util

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.sensibol.lucidmusic.singstr.gui.R


class ProgressBarAnimation(private val progressBar: ProgressBar, private val from: Int, private val to: Int) : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        val value = from + (to - from) * interpolatedTime
        progressBar.progress = value.toInt()
    }
}

fun ShapeableImageView.loadCenterCropImageFromUrl(imageUrl: String?) {
    Glide.with(this)
        .load(imageUrl)
        .placeholder(R.drawable.ic_profile_placeholder)
        .centerCrop()
        .into(this)
}