package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.song

import android.graphics.Rect
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.Song
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentSongsBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class SongsFragment : BaseFragment(), SongSelectorHostFragment.OnQueryChangeListener {

    override val layoutResId: Int = R.layout.fragment_songs
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentSongsBinding::inflate
    override val binding: FragmentSongsBinding get() = super.binding as FragmentSongsBinding

    private val viewModel: SongsViewModel by hiltNavGraphViewModels(R.id.song_selector_nav_graph)
    private val args: SongsFragmentArgs by navArgs()

    private var songPlayer: MediaPlayer? = null

    private var allowPlay: Boolean = false

    @Inject
    internal lateinit var songsAdapter: SongsAdapter

    override fun onInitView() {

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(previewUrl, ::playSongPreview)
        }
        songsAdapter.apply {
            onSongClickListener = {
                // FIXME - risky de-referencing?
                (parentFragment?.parentFragment as SongSelectorHostFragment).onSongSelected(it)
            }

            onSongPlayPauseListener = { songId, isPlay ->
                if (isPlay) {
                    if (songPlayer?.isPlaying == true)
                        songPlayer?.pause()
                    allowPlay = true
                    viewModel.loadSongPreviewUrl(songId)
                } else {
                    if (songPlayer?.isPlaying == true)
                        songPlayer?.pause()
                }
            }
        }

        binding.apply {
            rvTileSong.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val horizontalMargin = resources.getDimensionPixelSize(R.dimen.tile_song__horizontal_margin)
                        val verticalMargin = resources.getDimensionPixelSize(R.dimen.tile_song__vertical_margin)
                        val topMargin = if (parent.getChildAdapterPosition(view) == 0) verticalMargin else 0
                        outRect.set(horizontalMargin, topMargin, horizontalMargin, verticalMargin)
                    }
                })
                layoutManager = LinearLayoutManager(context)
                adapter = songsAdapter
            }

            ibBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            rgDifficultyLevel.setOnCheckedChangeListener { _, id ->
                viewModel.loadSongs(
                    difficulty = when (id) {
                        rbEasy.id -> Song.Difficulty.EASY
                        rbMed.id -> Song.Difficulty.MEDIUM
                        rbHard.id -> Song.Difficulty.HARD
                        else -> null
                    },
                    genre = viewModel.genre,
                    query = viewModel.query
                )
                pbLoading.visibility = VISIBLE
            }


            viewModel.apply {
                failure(failure, ::handleFailure)
                observe(songs) {
                    songsAdapter.songMinis = it
                    binding.pbLoading.visibility = GONE
                }
                loadSongs(genre = args.genre, query = args.query, difficulty = difficulty)
                pbLoading.visibility = VISIBLE
            }
        }

    }

    override fun onQueryChanged(query: String) {
        Timber.d("onQueryChanged: $query")
        viewModel.loadSongs(query = query, genre = viewModel.genre, difficulty = viewModel.difficulty)
        binding.pbLoading.visibility = VISIBLE
    }

    private fun playSongPreview(songUrl: String) {
        if (allowPlay) {
            if (songUrl.contains(".mp3"))
                songPlayer = MediaPlayer().apply {
                    setDataSource(songUrl)
                    setOnPreparedListener {
                        it.start()
                    }
                    prepareAsync()
                }
            else
                Toast.makeText(requireContext(), "Song preview not available!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        songPlayer?.pause()
        allowPlay = false
        songsAdapter.notifyPlayView()
    }

}