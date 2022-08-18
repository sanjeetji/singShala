package com.sensibol.lucidmusic.singstr.gui.app.learn.lesson

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.Exercise
import com.sensibol.lucidmusic.singstr.gui.databinding.TileVocalExcerciseBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class VocalExercisesAdapter @Inject constructor() : RecyclerView.Adapter<VocalExercisesAdapter.RecommendedSongVH>() {

    internal var onExerciseClickListener: (Exercise) -> Unit = {}

    var exercises: List<Exercise> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    class RecommendedSongVH(private val binding: TileVocalExcerciseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exercises: Exercise) {

            binding.tvVocalExercise.text = exercises.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecommendedSongVH(
        TileVocalExcerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            itemView.setOnClickListener {
                onExerciseClickListener(exercises[adapterPosition])
            }
        }

    override fun onBindViewHolder(holder: RecommendedSongVH, position: Int) = holder.bind(exercises[position])

    override fun getItemCount() = exercises.size
}