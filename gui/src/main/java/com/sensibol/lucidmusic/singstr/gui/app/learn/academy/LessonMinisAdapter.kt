package com.sensibol.lucidmusic.singstr.gui.app.learn.academy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.gui.databinding.TileLessonMiniBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

internal class LessonMinisAdapter @Inject constructor() :
    RecyclerView.Adapter<LessonMinisAdapter.LessonVH>() {

    internal var onLessonClickListener: (LessonMini) -> Unit = {}

    internal var lessons: List<LessonMini> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    inner class LessonVH(private val binding: TileLessonMiniBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lesson: LessonMini) {
            binding.apply {
                tvTitle.text = lesson.title
                tvDifficulty.text = lesson.difficulty
                tvDuration.text = lesson.duration
                tvType.text = lesson.type
                ivThumbnail.loadUrl(lesson.thumbnailUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LessonVH(TileLessonMiniBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            itemView.setOnClickListener {
                onLessonClickListener(lessons[adapterPosition])
            }
        }

    override fun getItemCount() = lessons.size

    override fun onBindViewHolder(holder: LessonVH, position: Int) = holder.bind(lessons[position])

}