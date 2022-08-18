package com.sensibol.lucidmusic.singstr.gui.app.learn.academy

import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.McqQuestion
import com.sensibol.lucidmusic.singstr.gui.databinding.TileAcademyAnswerOptionBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class AcademyAnswerOptionsAdapter @Inject constructor() : RecyclerView.Adapter<AcademyAnswerOptionsAdapter.OptionVH>() {

    internal var onOptionClickListener: (String, String) -> Unit = { _, _ -> }

    internal var options: List<McqQuestion.Option> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    var check:Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OptionVH(
        TileAcademyAnswerOptionBinding.inflate(parent.layoutInflater, parent, false)
    )

    inner class OptionVH(private val binding: TileAcademyAnswerOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(options: McqQuestion.Option) {
            binding.apply {
                tvOption.text = options.attributes.text
                tvOption.setOnClickListener {
                    onOptionClickListener(options.id, options.attributes.text)
                    /*if (check){
                        tvOption.setOnClickListener {
                            check = false
                            onOptionClickListener(options.id, options.attributes.text)
                        }*/
                }
            }
        }
    }

    override fun onBindViewHolder(holder: AcademyAnswerOptionsAdapter.OptionVH, position: Int) = holder.bind(options[position])

    override fun getItemCount(): Int = options.size
}
