package com.sensibol.lucidmusic.singstr.gui.app.learn.lesson

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.gui.databinding.TileRelatedSongBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

class RelatedSongsAdapter @Inject constructor() : RecyclerView.Adapter<RelatedSongsAdapter.RecommendedSongVH>() {

    internal var onPracticeSongClickListener: (SongMini) -> Unit = {}

    var songs: List<SongMini> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    class RecommendedSongVH(private val binding: TileRelatedSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: SongMini) {
            binding.tvPractice.text = song.title
            if (song.artists.isNullOrEmpty())
                binding.tvArtisit.visibility = GONE
            else
                binding.tvArtisit.text = song.artists[0].name
            binding.ivThumbnail.loadUrl(song.thumbnailUrl)
            binding.tvLyrics.text = song.lyrics
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecommendedSongVH(
        TileRelatedSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ).apply {
        itemView.setOnClickListener {
            onPracticeSongClickListener(songs[adapterPosition])
        }
    }

    override fun onBindViewHolder(holder: RecommendedSongVH, position: Int) = holder.bind(songs[position])

    override fun getItemCount() = songs.size
}