package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.song

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.gui.databinding.TileGenreBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import kotlin.properties.Delegates

@FragmentScoped
internal class GenresAdapter @Inject constructor() : RecyclerView.Adapter<GenresAdapter.GenreVH>() {

    internal var onGenreClickListener: (GenreView) -> Unit = {}

    internal var genreView: List<GenreView> by Delegates.observable(listOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal class GenreVH(private val binding: TileGenreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(genre: GenreView) {
            binding.tvTitle.text = genre.name
            binding.ivThumbnail.loadUrl(genre.thumbnailUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GenreVH(TileGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            itemView.setOnClickListener {
                onGenreClickListener(genreView[adapterPosition])
            }
        }

    override fun getItemCount() = genreView.size

    override fun onBindViewHolder(holder: GenreVH, position: Int) =
        holder.bind(genreView[position])
}