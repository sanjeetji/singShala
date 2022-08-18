package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentOnBoardGenreBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

// TODO - send selected genres to server

@AndroidEntryPoint
class OnBoardGenreFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_on_board_genre
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentOnBoardGenreBinding::inflate
    override val binding: FragmentOnBoardGenreBinding get() = super.binding as FragmentOnBoardGenreBinding

    @Inject
    lateinit var genreAdapter: GenreAdapter

    private var listObserver: MutableLiveData<HashSet<String>> = MutableLiveData<HashSet<String>>()

    private var selectedList: HashSet<String> = HashSet<String>()

    private val viewModel: OnBoardGenreViewModel by viewModels()

    override fun onInitView() {
        observe(listObserver, ::activeNextButton)
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(genres, { genreAdapter.genres = it })
            observe(addUserPreference, ::navigateToNextMove)
            loadGenres()
        }

        binding.apply {
            tvNext.setOnClickListener {
                if (selectedList.isNotEmpty()) {
                    val list: List<String> = ArrayList<String>(selectedList)
                    viewModel.addUserPreference(list, null)
                }
            }

            tvSkip.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.SkippedWalkThroughEvent(
                        Analytics.Event.Param.SkippedScreenName("Genre Selection")
                    )
                )
                findNavController().navigate(OnBoardGenreFragmentDirections.toOnBoardLanguageFragment())
            }

            rvSongType.apply {
                isNestedScrollingEnabled = false
                layoutManager = FlexboxLayoutManager(context).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.FLEX_START
                }
                adapter = genreAdapter
            }
            genreAdapter.onSongSelectChangeListener = { songType: Genre, isCheck: Boolean ->
                when (isCheck) {
                    true -> {
                        selectedList.add(songType.name)
                        listObserver.postValue(selectedList)
                    }
                    false -> {
                        selectedList.remove(songType.name)
                        listObserver.postValue(selectedList)
                    }
                }
                Timber.d("onGenreSelected $selectedList")
            }
        }

    }

    private fun navigateToNextMove(b: Boolean) {
        findNavController().navigate(OnBoardGenreFragmentDirections.toOnBoardLanguageFragment())
    }

    private fun activeNextButton(hashSet: java.util.HashSet<String>) {
        when (hashSet.size) {
            0 -> binding.tvNext.isEnabled = false
            else -> binding.tvNext.isEnabled = true
        }
    }

}