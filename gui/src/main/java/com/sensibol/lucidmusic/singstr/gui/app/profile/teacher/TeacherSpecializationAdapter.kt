package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.gui.databinding.ItemTextviewBinding
import javax.inject.Inject

class TeacherSpecializationAdapter @Inject constructor() : RecyclerView.Adapter<TeacherSpecializationAdapter.TeacherSpecializationVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TeacherSpecializationVH(
        ItemTextviewBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: TeacherSpecializationVH, position: Int) = holder.bind()

    override fun getItemCount(): Int = 3

    inner class TeacherSpecializationVH(private val binding: ItemTextviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }
}