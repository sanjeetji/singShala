package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.song

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentSongSelectorHostBinding
import timber.log.Timber

internal class SongSelectorHostFragment : BaseFragment() {

    internal interface OnQueryChangeListener {
        fun onQueryChanged(query: String)
    }

    override val layoutResId: Int get() = R.layout.fragment_song_selector_host
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentSongSelectorHostBinding::inflate
    override val binding: FragmentSongSelectorHostBinding get() = super.binding as FragmentSongSelectorHostBinding

    private val args: SongSelectorHostFragmentArgs by navArgs()

    override fun onInitView() {
        binding.apply {

            etSearchQuery.setOnEditorActionListener { v, actionId, _ ->

                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        ((childFragmentManager.findFragmentById(R.id.navHostFragmentSongSelector) as NavHostFragment)
                            .childFragmentManager.fragments[0] as OnQueryChangeListener).onQueryChanged(v.text.toString())

                        (v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(v.windowToken, 0)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    internal fun onSongSelected(song: SongMini) {
        Timber.d("onSongSelected: $song")
        findNavController().navigate(SongSelectorHostFragmentDirections.actionSongSelectorHostFragmentToSingModeFragment(song.id, args.singMode))
    }

}