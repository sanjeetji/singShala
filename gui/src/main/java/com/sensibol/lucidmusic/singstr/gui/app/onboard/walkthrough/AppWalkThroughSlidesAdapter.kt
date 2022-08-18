package com.sensibol.lucidmusic.singstr.gui.app.onboard.walkthrough

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.AppWalkThroughSlide
import com.sensibol.lucidmusic.singstr.gui.databinding.TileAppWalkThroughSlideBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

internal class AppWalkThroughSlidesAdapter @Inject constructor() : RecyclerView.Adapter<AppWalkThroughSlidesAdapter.AppWalkThroughViewHolder>() {

    internal var slides: List<AppWalkThroughSlide> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    inner class AppWalkThroughViewHolder(val binding: TileAppWalkThroughSlideBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(introSlide: AppWalkThroughSlide) {
            binding.apply {
                tvHeading.text = introSlide.title
                tvSubHeading.text = introSlide.description
                ivSlide.loadUrl(introSlide.imageUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AppWalkThroughViewHolder(
        TileAppWalkThroughSlideBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: AppWalkThroughViewHolder, position: Int) = holder.bind(slides[position])

    override fun getItemCount(): Int = slides.size

}