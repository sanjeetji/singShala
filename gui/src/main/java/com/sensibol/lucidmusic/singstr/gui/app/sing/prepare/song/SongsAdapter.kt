package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.song

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.lucidmusic.singstr.domain.model.Song
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.domain.model.names
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.TileSongBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.scopes.FragmentScoped
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@FragmentScoped
internal class SongsAdapter @Inject constructor() : RecyclerView.Adapter<SongsAdapter.SongVH>() {

    private var selectedPosition: Int = -1

    internal var onSongClickListener: (SongMini) -> Unit = {}

    internal var onSongPlayPauseListener: (String, Boolean) -> Unit = { songId, isPlay -> }

    internal var songMinis: List<SongMini> = emptyList()
        set(value) {
            // FIXME - Change to sorting by song.order
            field = value.sortedWith(compareBy { it.title })
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SongVH(TileSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            itemView.setOnClickListener {
                onSongClickListener(songMinis[adapterPosition])
            }
        }

    override fun getItemCount() = songMinis.size

    override fun onBindViewHolder(holder: SongVH, position: Int) =
        holder.bind(songMinis[position])

    internal fun notifyPlayView(){
        if(selectedPosition!=-1){
            val tempPos = selectedPosition
            selectedPosition = -1
            notifyItemChanged(tempPos)
        }
    }

    inner class SongVH(private val binding: TileSongBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(song: SongMini) {
            var heading = song.title
            val titleLength = heading.length
            heading += "  " + song.artists.names
            val headingLength = heading.length
            binding.tvHeading.text = SpannableStringBuilder(heading).apply {
                setSpan(StyleSpan(Typeface.BOLD), 0, titleLength, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                setSpan(
                    ForegroundColorSpan(Color.argb(128, 255, 255, 255)), titleLength, headingLength, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
            binding.tvLyrics.text = song.lyrics

            when (song.difficulty.toUpperCase(Locale.ROOT)) {
                Song.Difficulty.EASY.name -> {
                    binding.level1.alpha = 1f
                    binding.level2.alpha = 0.11F
                    binding.level3.alpha = 0.11F
                }
                Song.Difficulty.MEDIUM.name -> {
                    binding.level1.alpha = 1f
                    binding.level2.alpha = 1f
                    binding.level3.alpha = 0.11F
                }
                Song.Difficulty.HARD.name -> {
                    binding.level1.alpha = 1f
                    binding.level2.alpha = 1f
                    binding.level3.alpha = 1f
                }
                else -> { // Note the block
                    Timber.e("Unknown difficulty ${song.difficulty} for song ${song.id}")
                    binding.level1.alpha = 0f
                    binding.level2.alpha = 0f
                    binding.level3.alpha = 0f
                }
            }
            binding.ivThumbnail.loadUrl(song.thumbnailUrl, R.drawable.ic_audiotrack)

            binding.ivPause.apply {
                visibility = if (selectedPosition == adapterPosition) VISIBLE else GONE

                setOnClickListener {
                    if (selectedPosition == adapterPosition) {
                        onSongPlayPauseListener(song.id, false)
                        visibility = GONE
                        binding.ivPlay.visibility = VISIBLE
                        selectedPosition = -1
                    }
                }
            }

            binding.ivPlay.apply {
                visibility = if (selectedPosition != adapterPosition) VISIBLE else GONE

                setOnClickListener {
                    if (selectedPosition == adapterPosition) {
                        onSongPlayPauseListener(song.id, true)

                    } else {
                        if (selectedPosition != -1)
                            notifyItemChanged(selectedPosition)
                        selectedPosition = adapterPosition
                        visibility = GONE
                        binding.ivPause.visibility = VISIBLE
                        onSongPlayPauseListener(song.id, true)
                    }
                }
            }
        }
    }

}