package com.sensibol.lucidmusic.singstr.gui.app.exftr

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.gui.databinding.TileExclusiveFeatureBinding
import timber.log.Timber
import javax.inject.Inject

class ExclusiveFeaturesAdapter @Inject constructor() :
    RecyclerView.Adapter<ExclusiveFeaturesAdapter.FeaturesVH>() {

    internal var onClickListener: (ExclusiveFeature) -> Unit = { exclusiveFeatures -> }

    var exclusiveFeature: List<ExclusiveFeature> = ExclusiveFeaturesFragment.exclusiveFeatureList

    inner class FeaturesVH(val binding: TileExclusiveFeatureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exclusiveFeature: ExclusiveFeature) {
            binding.apply {
                tvTitle.text = exclusiveFeature.title
                tvDesc.setText(exclusiveFeature.descriptionResId)
                ivFeature.setImageResource(exclusiveFeature.bannerImageResId)
                tvCTATitle.text = exclusiveFeature.ctaTitle
                tvCTATitle.setOnClickListener {
                    onClickListener(exclusiveFeature)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturesVH = FeaturesVH(
        TileExclusiveFeatureBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun getItemCount() = exclusiveFeature.size

    override fun onBindViewHolder(holder: FeaturesVH, position: Int) = holder.bind(exclusiveFeature[position]).also {
        Timber.d("onBindViewHolder: $position")
    }
}