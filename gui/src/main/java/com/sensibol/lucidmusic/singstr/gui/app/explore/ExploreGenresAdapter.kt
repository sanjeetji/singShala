package com.sensibol.lucidmusic.singstr.gui.app.explore

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.ItemSongCategoryBinding
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import javax.inject.Inject
import kotlin.properties.Delegates

class ExploreGenresAdapter @Inject constructor() : RecyclerView.Adapter<ExploreGenresAdapter.ExploreSongGenreVH>() {

    internal var onGenreClickListener: (Genre) -> Unit = {}

    private var checkedPosition: Int = 0
    internal var genres: List<Genre> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExploreSongGenreVH(
        ItemSongCategoryBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: ExploreSongGenreVH, position: Int) = holder.bind(genres[position])

    override fun getItemCount(): Int = genres.size

    inner class ExploreSongGenreVH(private val binding: ItemSongCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(genre: Genre) {
            binding.apply {
                tvGenreName.text = genre.name
                tvGenreName2.text = genre.name
                if (tvGenreName.text == "All") {
                    tvGenreName.setTextColor(Color.parseColor("#FF000000"))
                    tvGenreName.background.alpha = 0
                } else {
                    tvGenreName.setTextColor(Color.parseColor("#FFFFFFFF"))
                    tvGenreName.background.alpha = 30
                    tvGenreName.setBackgroundColor(Color.parseColor("#B3222222"))
                }
                ivGenreThumbnail.loadUrl(genre.thumbnail)

                if(tvGenreName2.text == "Trending"){
                    tvGenreName2.compoundDrawablePadding = 16
                    tvGenreName2.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_explore_trending, 0, 0, 0)
                }else
                    tvGenreName2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                if (checkedPosition == adapterPosition) {
                    vline.visibility = View.VISIBLE
                } else {
                    vline.visibility = View.GONE
                }
                ivGenreThumbnail.setOnClickListener {
                    ivGenreThumbnail.loadUrl(genre.thumbnail)
                    vline.visibility = View.VISIBLE
                    if (checkedPosition != adapterPosition) {
                        notifyItemChanged(checkedPosition)
                        checkedPosition = adapterPosition
                    }
                    onGenreClickListener(genres[adapterPosition])
                }
                tvGenreName2.setOnClickListener {
                    ivGenreThumbnail.loadUrl(genre.thumbnail)
                    vline.visibility = View.VISIBLE
                    if (checkedPosition != adapterPosition) {
                        notifyItemChanged(checkedPosition)
                        checkedPosition = adapterPosition
                    }
                    onGenreClickListener(genres[adapterPosition])
                }
            }
        }
    }
}