package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.gui.databinding.TileOnBoardOptionBinding
import javax.inject.Inject
import kotlin.properties.Delegates

class GenreAdapter @Inject constructor() : RecyclerView.Adapter<GenreAdapter.GenreVH>() {

    internal var onSongSelectChangeListener: (Genre, isCheck: Boolean) -> Unit = { genre, isCheck -> }

    internal var genres: List<Genre> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    inner class GenreVH(private val binding: TileOnBoardOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(genre: Genre) {
            binding.cbOption.text = genre.name
            binding.cbOption.setOnCheckedChangeListener { btn, isChecked ->
                onSongSelectChangeListener(genre, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GenreVH(
        TileOnBoardOptionBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: GenreVH, position: Int) = holder.bind(genres[position])

    override fun getItemCount(): Int = genres.size
}