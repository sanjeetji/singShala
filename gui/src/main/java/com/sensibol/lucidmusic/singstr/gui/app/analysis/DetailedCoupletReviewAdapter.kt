package com.sensibol.lucidmusic.singstr.gui.app.analysis

import android.media.MediaPlayer
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.DetailedCoupletReview
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.TileDetailedBinding
import com.sensibol.lucidmusic.singstr.gui.timeMS2mmss
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

class DetailedCoupletReviewAdapter @Inject constructor() : RecyclerView.Adapter<DetailedCoupletReviewAdapter.DetailAnalysisVH>() {

    private var checkedPosition: Int = 0

    internal var onPreviewClickListener: (TextView, Int, Int) -> Unit = {textView, startTimeMS, endTimeMS -> }

    internal var onCorrectionClickListener: (TextView, Int, Int) -> Unit = {textView, startTimeMS, endTimeMS -> }

    internal var songReview: List<DetailedCoupletReview> by Delegates.observable(listOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailAnalysisVH = DetailAnalysisVH(
        TileDetailedBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: DetailAnalysisVH, position: Int) = holder.bind(songReview[position])

    override fun getItemCount(): Int = songReview.size

    inner class DetailAnalysisVH(private val binding: TileDetailedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: DetailedCoupletReview) {
            binding.apply {
                if (checkedPosition == adapterPosition) {
                    llcCTA.visibility = View.VISIBLE
                } else {
                    llcCTA.visibility = View.GONE
                }
                ivCircle.setImageResource(
                    when (review.remark.toLowerCase(Locale.ROOT)) {
                        "good" -> R.drawable.green_circle
                        "average" -> R.drawable.yellow_circle
                        else -> R.drawable.red_circle
                    }
                )
                tvTitle.text = review.lyrics
                tvTune.text = review.reviewComment
                tvTiming.text = timeMS2mmss(review.startTimeMS)
                cvTile.setOnClickListener {
                    llcCTA.visibility = View.VISIBLE
                    if (checkedPosition != adapterPosition) {
                        notifyItemChanged(checkedPosition)
                        checkedPosition = adapterPosition
                    }
                }
                tvPreview.setOnClickListener {
                    tvCorrection.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_small, 0, 0, 0)
                    tvPreview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_small, 0, 0, 0)
                    onPreviewClickListener(tvPreview, review.startTimeMS, review.endTimeMS)
                }
                tvCorrection.setOnClickListener {
                    tvPreview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_small, 0, 0, 0)
                    tvCorrection.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_small, 0, 0, 0)
                    onCorrectionClickListener(tvCorrection, review.startTimeMS, review.endTimeMS)
                }
            }
        }
    }

}