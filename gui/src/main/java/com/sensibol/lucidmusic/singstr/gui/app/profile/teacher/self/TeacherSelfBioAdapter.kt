package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.CoverView
import com.sensibol.lucidmusic.singstr.gui.databinding.TileCoverProfileBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.TileTeacherBioBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import kotlin.properties.Delegates

@FragmentScoped
class TeacherSelfBioAdapter @Inject constructor() : RecyclerView.Adapter<TeacherSelfBioAdapter.TeacherSelfBioVH>() {

    internal var onClickListener: () -> Unit = {}


    class TeacherSelfBioVH(private val binding: TileTeacherBioBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(){//bind(coverView: CoverView) {
//            binding.teacherImg.setImageResource(R.drawable.teacher_img)
            binding.tvHeading.text = "Heading"
//            binding.txvSubDetail.text = "Details One"
//            binding.txvSubDetail1.text = "Details Two"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TeacherSelfBioVH(
        TileTeacherBioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TeacherSelfBioVH, position: Int) = holder.bind()//coverViews[position]

    override fun getItemCount() = 8//coverViews.size


}