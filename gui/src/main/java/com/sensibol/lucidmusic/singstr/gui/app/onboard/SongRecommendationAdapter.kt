package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.gui.databinding.TileObBoardSongBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

class SongRecommendationAdapter @Inject constructor() : RecyclerView.Adapter<SongRecommendationAdapter.SongRecommendationVH>() {

    internal var onSingClickListener: (SongMini) -> Unit = { song -> }

    internal var songs: List<SongMini> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongRecommendationVH =
        SongRecommendationVH(TileObBoardSongBinding.inflate(parent.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: SongRecommendationVH, position: Int) {
        holder.bind(songs.get(position))
    }

    override fun getItemCount(): Int = songs.size

    inner class SongRecommendationVH(private val binding: TileObBoardSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: SongMini) {
            binding.apply {
                tvTitle.text = song.title
                ivImage.loadUrl(song.thumbnailUrl)
                tvArtistName.text = song.artists.get(0).name
                tvSing.setOnClickListener {
                    onSingClickListener(song)
                }
            }
        }

    }
}