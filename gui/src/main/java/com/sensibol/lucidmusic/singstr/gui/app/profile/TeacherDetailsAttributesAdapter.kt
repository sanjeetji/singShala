package com.sensibol.lucidmusic.singstr.gui.app.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.sensibol.lucidmusic.singstr.gui.databinding.TileTeacherAttributeDetailsBinding
import javax.inject.Inject
import kotlin.properties.Delegates


internal class TeacherDetailsAttributesAdapter @Inject constructor() : RecyclerView.Adapter<TeacherDetailsAttributesAdapter.AttributeVH>() {

    internal var attributesDetails: List<TeacherDetails.Attributes.Details> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    internal class AttributeVH(private val binding: TileTeacherAttributeDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
                fun bind(attributesDetails: TeacherDetails.Attributes.Details){
                    binding.apply {
                        tvTitle.text = attributesDetails.title
                        tvSubtitle.text = attributesDetails.sub_title
                    }
                }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AttributeVH(TileTeacherAttributeDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            .apply {

            }

    override fun getItemCount()= attributesDetails.size

    override fun onBindViewHolder(holder: AttributeVH, position: Int) = holder.bind(attributesDetails[position])
}