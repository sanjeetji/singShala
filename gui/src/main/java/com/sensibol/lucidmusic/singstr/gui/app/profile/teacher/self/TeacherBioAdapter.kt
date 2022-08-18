package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.sensibol.lucidmusic.singstr.gui.app.profile.TeacherDetailsAttributesAdapter
import com.sensibol.lucidmusic.singstr.gui.databinding.TileTeacherBioBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.TileTeacherExperienceBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import kotlin.properties.Delegates

@FragmentScoped
class TeacherBioAdapter @Inject constructor() : RecyclerView.Adapter<TeacherBioAdapter.TeacherDetailsVH>() {

    var teacherDetail: List<TeacherDetails.Attributes> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    class TeacherDetailsVH(private val binding: TileTeacherBioBinding) : RecyclerView.ViewHolder(binding.root) {

        private val detailsAttributeAdapter: TeacherDetailsAttributesAdapter = TeacherDetailsAttributesAdapter()

        fun bind(teacherDetail: TeacherDetails.Attributes) {
            binding.apply {
                tvHeading.text = teacherDetail.title
                if(teacherDetail.image.isNotBlank()){
                    ivAttribute.apply {
                        visibility = View.VISIBLE
                        loadUrl(teacherDetail.image)
                    }
                }
                rvTeacherAttributes.adapter = detailsAttributeAdapter.apply {
                    attributesDetails = teacherDetail.details
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TeacherDetailsVH(
        TileTeacherBioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TeacherDetailsVH, position: Int) = holder.bind(teacherDetail[position])

    override fun getItemCount() = teacherDetail.size
}