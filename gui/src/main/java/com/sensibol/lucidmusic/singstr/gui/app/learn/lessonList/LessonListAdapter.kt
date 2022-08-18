package com.sensibol.lucidmusic.singstr.gui.app.learn.lessonList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.gui.databinding.TileLessonBigBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

internal class LessonListAdapter @Inject constructor() :
    RecyclerView.Adapter<LessonListAdapter.LessonVH>(), Filterable {

    internal var onLessonClickListener: (LessonMini) -> Unit = {}

    internal var lessons: List<LessonMini> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }
    val originalList = lessons
    inner class LessonVH(private val binding: TileLessonBigBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lesson: LessonMini) {
            binding.apply {
                tvTitle.text = lesson.title
                tvDifficulty.text = lesson.difficulty
                tvDuration.text = lesson.duration
                ivLessonThumbnail.loadUrl(lesson.thumbnailUrl)
                tvFreeLabel.text = lesson.subscriptionType
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

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val results = FilterResults()
                if (constraint.isEmpty()) {
                    //no filter implemented we return full list
                    results.values = lessons
                    results.count = lessons!!.size
                } else {
                    //Here we perform filtering operation
                    val list: ArrayList<LessonMini> = ArrayList()
                    for (p in lessons!!) {
                        if (p.title.toUpperCase().startsWith(constraint.toString().toUpperCase())) list.add(p)
                    }
                    results.values = list
                    results.count = list.size
                }
                return results

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results!!.count == 0 || constraint == "") {
                    lessons = originalList
                    notifyDataSetChanged()
                } else {
                    lessons = (results.values as ArrayList<LessonMini>?)!!
                    notifyDataSetChanged()
                }
            }
        }

    }
        }