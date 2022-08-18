package com.sensibol.lucidmusic.singstr.gui.app.learn.lesson

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.BottomsheetSongTileLearnBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

class RecommendedSongsAdapter @Inject constructor() : RecyclerView.Adapter<RecommendedSongsAdapter.RecommendedSongVH>() {

    internal var onClickListener: (SongMini) -> Unit = {}

    var songs: List<SongMini> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    class RecommendedSongVH(private val binding: BottomsheetSongTileLearnBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: SongMini) {
            binding.tvSongTitle.text = song.title
            binding.ivSongThumbnail.loadUrl(song.thumbnailUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecommendedSongVH(
        BottomsheetSongTileLearnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ).apply {
       itemView.findViewById<AppCompatTextView>(R.id.tv_sing_button).setOnClickListener{
           onClickListener(songs[adapterPosition])
       }
    }

    override fun onBindViewHolder(holder: RecommendedSongVH, position: Int) = holder.bind(songs[position])

    override fun getItemCount() = songs.size
}