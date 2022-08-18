 package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.sensibol.lucidmusic.singstr.gui.databinding.TileTeacherProfessionalDetailBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class TeacherProfessionalDetailsAdapter @Inject constructor() : RecyclerView.Adapter<TeacherProfessionalDetailsAdapter.TeacherDetailsVH>() {

    internal var onClickListener: () -> Unit = {}
    var teacherDetails: List<TeacherDetails.Attributes> by Delegates.observable( emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    class TeacherDetailsVH(private val binding: TileTeacherProfessionalDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        @Inject
        internal lateinit var experienceDetailsAdapter: TeacherDetailsAdapter

        fun bind(teacherProfessionalDetails: TeacherDetails.Attributes) {
            experienceDetailsAdapter = TeacherDetailsAdapter(teacherProfessionalDetails.title)
            experienceDetailsAdapter.professionalDetailItems = teacherProfessionalDetails.details
            binding.txvDetailName.setText(teacherProfessionalDetails.title)
            binding.rvTeacherExperience.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = experienceDetailsAdapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TeacherDetailsVH(
        TileTeacherProfessionalDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TeacherDetailsVH, position: Int) = holder.bind(teacherDetails[position])

    override fun getItemCount() = teacherDetails.size
}