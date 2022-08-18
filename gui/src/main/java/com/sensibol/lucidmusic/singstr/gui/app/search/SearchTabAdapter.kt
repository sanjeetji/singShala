package com.sensibol.lucidmusic.singstr.gui.app.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.SearchTags
import com.sensibol.lucidmusic.singstr.gui.databinding.TabBarLearnBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class SearchTabAdapter @Inject constructor() : RecyclerView.Adapter<SearchTabAdapter.LearnVH>() {

    internal var onSearchFilterTagClickListener: (Boolean, String) -> Unit = { isChecked, tagName -> }

    internal var searchTags: List<SearchTags> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LearnVH(
        TabBarLearnBinding.inflate(parent.layoutInflater, parent, false)
    )

    inner class LearnVH(private val binding: TabBarLearnBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lesson: SearchTags) {
            binding.apply {
                tvLessonName.text = lesson.title
                tvLessonName.isChecked = lesson.isCheck
                tvLessonName.setOnClickListener {
                    if (adapterPosition == 0) {
                        tvLessonName.isChecked = true
                        lesson.isCheck = true
                        for (i in 1 until searchTags.size) {
                            searchTags[i].isCheck = false
                            notifyItemChanged(i)
                        }
                    } else {
                        lesson.isCheck = tvLessonName.isChecked
                        searchTags[0].isCheck = false
                        notifyItemChanged(0)
                    }
                    onSearchFilterTagClickListener(lesson.isCheck, lesson.title)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: SearchTabAdapter.LearnVH, position: Int) = holder.bind(searchTags[position])

    override fun getItemCount(): Int = searchTags.size
}
