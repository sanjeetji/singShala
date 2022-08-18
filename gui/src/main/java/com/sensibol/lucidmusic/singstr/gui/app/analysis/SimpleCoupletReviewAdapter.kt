package com.sensibol.lucidmusic.singstr.gui.app.analysis

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.SimpleCoupletReview
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.TileMinidetailTextviewBinding
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

class SimpleCoupletReviewAdapter @Inject constructor() : RecyclerView.Adapter<SimpleCoupletReviewAdapter.MiniDetailAnalysisVH>() {

    internal var simpleCoupletReview: List<SimpleCoupletReview> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniDetailAnalysisVH = MiniDetailAnalysisVH(
        TileMinidetailTextviewBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: MiniDetailAnalysisVH, position: Int) = holder.bind(simpleCoupletReview[position])

    override fun getItemCount() = simpleCoupletReview.size

    inner class MiniDetailAnalysisVH(private var binding: TileMinidetailTextviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(simpleCoupletReview: SimpleCoupletReview) {
            binding.apply {
                tvRemark.text = simpleCoupletReview.lyrics
                ivRemarkCategory.setImageResource(
                    when (simpleCoupletReview.remark.toLowerCase(Locale.ROOT)) {
                        "good" -> R.drawable.ic_review_good
                        "average" -> R.drawable.ic_review_average
                        else -> R.drawable.ic_review_bad
                    }
                )
            }
        }
    }
}