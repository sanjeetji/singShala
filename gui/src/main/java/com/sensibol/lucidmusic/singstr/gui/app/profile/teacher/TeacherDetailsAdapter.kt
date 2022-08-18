 package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.sensibol.lucidmusic.singstr.gui.databinding.TileTeacherExperienceBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class TeacherDetailsAdapter @Inject constructor(detailsOf: String) : RecyclerView.Adapter<TeacherDetailsAdapter.TeacherDetailsVH>() {

    internal var onClickListener: () -> Unit = {}

    var professionalDetailItems: List<TeacherDetails.Attributes.Details> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    class TeacherDetailsVH(private val binding: TileTeacherExperienceBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(teacherProfessionalDetailItem: TeacherDetails.Attributes.Details) {
            binding.txvName.setText(teacherProfessionalDetailItem.title)
            binding.txvSubDetail.setText(teacherProfessionalDetailItem.sub_title)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TeacherDetailsVH(
        TileTeacherExperienceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TeacherDetailsVH, position: Int) = holder.bind(professionalDetailItems[position])

    override fun getItemCount() = professionalDetailItems.size
}