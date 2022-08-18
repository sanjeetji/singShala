package com.sensibol.lucidmusic.singstr.gui.app.learn.academy

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.LessonTags
import com.sensibol.lucidmusic.singstr.gui.databinding.TabBarLearnBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class LessonTabAdapter @Inject constructor() : RecyclerView.Adapter<LessonTabAdapter.LearnVH>() {

    internal var onLessonCategoryClickListener: (Boolean, String) -> Unit = { isChecked, tagName -> }

    internal var lessonCat: List<LessonTags> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LearnVH(
        TabBarLearnBinding.inflate(parent.layoutInflater, parent, false)
    )

    inner class LearnVH(private val binding: TabBarLearnBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lesson: LessonTags) {
            binding.apply {
                tvLessonName.text = lesson.title
                tvLessonName.isChecked = lesson.isCheck
                tvLessonName.setOnClickListener {
                    if (adapterPosition == 0) {
                        tvLessonName.isChecked = true
                        lesson.isCheck = true
                        for (i in 1 until lessonCat.size) {
                            lessonCat[i].isCheck = false
                            notifyItemChanged(i)
                        }
                    } else {
                        lesson.isCheck = tvLessonName.isChecked
                        lessonCat[0].isCheck = false
                        notifyItemChanged(0)
                    }
                    onLessonCategoryClickListener(lesson.isCheck, lesson.title)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: LessonTabAdapter.LearnVH, position: Int) = holder.bind(lessonCat[position])

    override fun getItemCount(): Int = lessonCat.size
}
