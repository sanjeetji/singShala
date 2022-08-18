package com.sensibol.lucidmusic.singstr.gui.app.profile.self.drafts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.Draft
import com.sensibol.lucidmusic.singstr.domain.model.NodeDraft
import com.sensibol.lucidmusic.singstr.domain.model.names
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCoverDraftBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

class NodeDraftsAdapter @Inject constructor() :
    RecyclerView.Adapter<NodeDraftsAdapter.DraftVH>() {

    internal var onPublishClickListener: (draft: NodeDraft) -> Unit = {}

    internal var onAnalyseClickListener: (draft: NodeDraft) -> Unit = {}

    internal var onDeleteClickListener: (draft: NodeDraft) -> Unit = {}

    var drafts: List<NodeDraft> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    inner class DraftVH(private val binding: TileCoverDraftBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                btnAnalyse.setOnClickListener { onAnalyseClickListener(drafts[absoluteAdapterPosition]) }
                btnPublish.setOnClickListener { onPublishClickListener(drafts[absoluteAdapterPosition]) }
                ibDelete.setOnClickListener { onDeleteClickListener(drafts[absoluteAdapterPosition]) }
            }
        }

        internal fun bind(draft: NodeDraft) {
            Analytics.setUserProperty(Analytics.UserProperty.DraftCovers(drafts.size))
            binding.apply {
                tvTitle.text = draft.songTitle
                tvArtists.text = ""
                tvXpGained.text = draft.totalScore.toString()
                ivThumbnail.loadUrl(draft.coverUrl)
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT)
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    val past = sdf.parse(draft.attemptTime)
                    sdf.timeZone = TimeZone.getDefault();
                    val now = Date()

                    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
                    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
                    val hours: Long = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
                    val days: Long = TimeUnit.MILLISECONDS.toDays(now.time - past.time)

                    tvCreatedTime.text = when {
                        seconds < 60 -> {
                            tvCreatedTime.context.getString(R.string.create_n_time_age, seconds, "seconds")
                        }
                        minutes < 60 -> {
                            tvCreatedTime.context.getString(R.string.create_n_time_age, minutes, "minutes")
                        }
                        hours < 24 -> {
                            tvCreatedTime.context.getString(R.string.create_n_time_age, hours, "hours")
                        }
                        else -> {
                            tvCreatedTime.context.getString(R.string.create_n_time_age, days, "days")
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error setting the created date.")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DraftVH(TileCoverDraftBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = drafts.size

    override fun onBindViewHolder(holder: DraftVH, position: Int) {
        holder.bind(drafts[position])
    }
}