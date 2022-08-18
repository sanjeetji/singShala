package com.sensibol.lucidmusic.singstr.gui.app.learn.academy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.gui.databinding.TileLessonBigBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

internal class TuneTimingAdapter @Inject constructor() :
    RecyclerView.Adapter<TuneTimingAdapter.LessonVH>() {

    internal var onLessonClickListener: (LessonMini) -> Unit = {}

    internal var lessons: List<LessonMini> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    inner class LessonVH(private val binding:TileLessonBigBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lesson: LessonMini) {
            binding.apply {
                tvTitle.text = lesson.title
                tvDifficulty.text = lesson.difficulty
                tvDuration.text = lesson.duration
                ivLessonThumbnail.loadUrl(lesson.thumbnailUrl)
                tvType.text=lesson.type
                if(lesson.subscriptionType=="Free")
                {
                    tvFreeLabel.text=lesson.subscriptionType
                }
                else
                {
                    tvFreeLabel.visibility= View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LessonVH(TileLessonBigBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            itemView.setOnClickListener {
                onLessonClickListener(lessons[adapterPosition])
            }
        }

    override fun getItemCount() = lessons.size

    override fun onBindViewHolder(holder: LessonVH, position: Int) = holder.bind(lessons[position])


}