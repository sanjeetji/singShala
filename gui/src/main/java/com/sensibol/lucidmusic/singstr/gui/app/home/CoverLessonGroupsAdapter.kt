package com.sensibol.lucidmusic.singstr.gui.app.home

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.learn.academy.LessonMinisAdapter
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCoverLessonGroupBinding
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

class CoverLessonGroupsAdapter @Inject constructor() :
    RecyclerView.Adapter<CoverLessonGroupsAdapter.CoverLessonGroupsVH>() {

    var onLessonClickListener: (LessonMini) -> Unit = {}

    var onAllViewLessonClickListener: (String) -> Unit = {}

    internal var onCoverClickListener: (cover: Cover, position: Int) -> Unit = { _, _ -> }

//    internal var lessonGroups: List<AcademyContent.LessonGroup> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    internal var conceptGroups: List<ConceptInfo> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    internal var feedGroups: List<Feed> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    inner class CoverLessonGroupsVH(private val binding: TileCoverLessonGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val lessonMinisAdapter: LessonMinisAdapter = LessonMinisAdapter()
        private val feedsCoverAdapter: FeedCoversAdapter = FeedCoversAdapter()

        fun bind(conceptGroup: ConceptInfo,feedGroup:Feed) {
            binding.apply {
                tvTitle.text = conceptGroup.name
                rvLearnLesson.adapter = lessonMinisAdapter.apply {
                    onLessonClickListener = this@CoverLessonGroupsAdapter.onLessonClickListener
                    lessons = conceptGroup.lessonList
                }
                rvTrendingCovers.adapter = feedsCoverAdapter.apply {
                    onCoverClickListener = this@CoverLessonGroupsAdapter.onCoverClickListener
                    covers = feedGroup.covers
                }
                tvViewAll.setOnClickListener {
                    onAllViewLessonClickListener(conceptGroup.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverLessonGroupsVH =
    CoverLessonGroupsVH(TileCoverLessonGroupBinding.inflate(parent.layoutInflater, parent, false).apply {
            rvLearnLesson.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        rvTrendingCovers.apply {
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


    override fun onBindViewHolder(holder: CoverLessonGroupsVH, position: Int){

        holder.bind(conceptGroups[position],feedGroups[position])
    }

    override fun getItemCount(): Int = conceptGroups.size
}