package com.sensibol.lucidmusic.singstr.gui.login

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.gui.databinding.VpTileLoginBinding
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
internal class LoginBannersAdapter @Inject constructor() : RecyclerView.Adapter<LoginBannersAdapter.CarouselBannerVH>() {

    inner class CarouselBannerVH(private val binding: VpTileLoginBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                if (position == 1){
                    tvLearnSing.text = "Learn to Sing with Video Lessons & Exercises"
                }else if (position == 2){
                    tvLearnSing.text = "Unlimited Singing Practice with Guided Lyrics"
                }else if (position == 3){
                    tvLearnSing.text = "Get Line by Line Feedback with Singing Score"
                }else if (position == 4){
                    tvLearnSing.text = "Participate in Singing Contests & Win Prizes"
                }else if (position == 4){
                    tvLearnSing.text = "Create Covers and share it with others in the community"
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CarouselBannerVH(
        VpTileLoginBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: CarouselBannerVH, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = 5
}

