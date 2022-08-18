package com.sensibol.lucidmusic.singstr.gui.app.home

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.CarouselBanner
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCorouselBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import kotlin.properties.Delegates

@FragmentScoped
internal class CarouselBannersAdapter @Inject constructor() : RecyclerView.Adapter<CarouselBannersAdapter.CarouselBannerVH>() {

    internal var onBannerClickListener: (banner: CarouselBanner) -> Unit = { _ -> }

    internal var banners: List<CarouselBanner> by Delegates.observable(arrayListOf()) { _, _, _ -> notifyDataSetChanged() }

    inner class CarouselBannerVH(private val binding: TileCorouselBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(carouselBanner: CarouselBanner) {
            binding.apply {
                ivCarouselImage.loadUrl(
                    carouselBanner.thumbnailUrl,
                    placeholderResId = R.drawable.bg_rounded_card,
                    scaleType = ImageView.ScaleType.FIT_CENTER)
                itemView.setOnClickListener {
                    onBannerClickListener(carouselBanner)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CarouselBannerVH(
        TileCorouselBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: CarouselBannerVH, position: Int) {
        holder.bind(banners[position])
    }

    override fun getItemCount(): Int = banners.size
}