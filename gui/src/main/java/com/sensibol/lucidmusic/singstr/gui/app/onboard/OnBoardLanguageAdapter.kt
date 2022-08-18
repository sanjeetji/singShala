package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.gui.databinding.TileOnBoardOptionBinding
import javax.inject.Inject

class OnBoardLanguageAdapter @Inject constructor() : RecyclerView.Adapter<OnBoardLanguageAdapter.LanguageVH>() {

    internal var onLanguageCheckChangeListener: (String, isCheck: Boolean) -> Unit = { language, isCheck -> }

    internal val language: List<String> = listOf("Hindi", "English")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LanguageVH(
        TileOnBoardOptionBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: LanguageVH, position: Int) = holder.bind(language[position])

    override fun getItemCount(): Int = language.size

    inner class LanguageVH(private val binding: TileOnBoardOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(language: String) {
            binding.cbOption.text = language
            binding.cbOption.setOnCheckedChangeListener { btn, isChecked ->
                onLanguageCheckChangeListener(language, isChecked)
            }
        }

    }
}