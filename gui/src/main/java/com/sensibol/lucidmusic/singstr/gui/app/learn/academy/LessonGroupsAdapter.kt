package com.sensibol.lucidmusic.singstr.gui.app.learn.academy

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.AcademyContent
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.TileLessonGroupBinding
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

class LessonGroupsAdapter @Inject constructor() :
    RecyclerView.Adapter<LessonGroupsAdapter.LessonGroupVH>() {

    var onLessonClickListener: (LessonMini) -> Unit = {}

    var onAllViewLessonClickListener: (String) -> Unit = {}

    internal var lessonGroups: List<AcademyContent.LessonGroup> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    inner class LessonGroupVH(private val binding: TileLessonGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val lessonMinisAdapter: LessonMinisAdapter = LessonMinisAdapter()

        fun bind(lessonGroup: AcademyContent.LessonGroup) {
            binding.apply {
                tvTitle.text = lessonGroup.title
                rvLearnLesson.adapter = lessonMinisAdapter.apply {
                    onLessonClickListener = this@LessonGroupsAdapter.onLessonClickListener
                    lessons = lessonGroup.lessons
                }
                tvViewAll.setOnClickListener {
                    onAllViewLessonClickListener(lessonGroup.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonGroupVH =
        LessonGroupVH(TileLessonGroupBinding.inflate(parent.layoutInflater, parent, false).apply {
            rvLearnLesson.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

        }).also {
            Timber.d("onCreateViewHolder: ")
        }

    override fun onBindViewHolder(holder: LessonGroupVH, position: Int) = holder.bind(lessonGroups[position]).also {
        Timber.d("onBindViewHolder: $position")
    }

    override fun getItemCount(): Int = lessonGroups.size
}