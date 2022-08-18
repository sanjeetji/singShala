package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.song

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentGenresBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
internal class GenresFragment : BaseFragment(), SongSelectorHostFragment.OnQueryChangeListener {

    override val layoutResId: Int get() = R.layout.fragment_genres
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentGenresBinding::inflate
    override val binding: FragmentGenresBinding get() = super.binding as FragmentGenresBinding

    private val viewModel: GenresViewModel by viewModels()

    @Inject
    internal lateinit var genresAdapter: GenresAdapter

    override fun onInitViewModel() {
        super.onInitViewModel()
        viewModel.loadGenres()
    }

    override fun onInitView() {
        binding.apply {
            pbLoading.visibility = VISIBLE

            genresAdapter.onGenreClickListener = {
                Analytics.logEvent(
                    Analytics.Event.GenreSelectionEvent(
                        Analytics.Event.Param.GenreId(it.genre.name)
                    )
                )
                findNavController().navigate(GenresFragmentDirections.actionGenresFragmentToSongsFragment(genre = it.genre))
            }

            btnAllSongs.setOnClickListener() {
                findNavController().navigate(GenresFragmentDirections.actionGenresFragmentToSongsFragment())
            }

            rvTileGenre.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = genresAdapter
            }

            viewModel.apply {
                failure(failure, ::handleFailure)
                observe(genreViews, ::showGenres)
            }
        }

    }

    private fun showGenres(genreViews: List<GenreView>) {
        Timber.d("showGenres: $genreViews")
        genresAdapter.genreView = genreViews
        binding.pbLoading.visibility = GONE
    }

    override fun onQueryChanged(query: String) {
        Timber.d("onQueryChanged: $query")
        findNavController().navigate(GenresFragmentDirections.actionGenresFragmentToSongsFragment(query = query))
    }

}