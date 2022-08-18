package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.song

import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.usecase.GetGenresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class GenresViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase
) : BaseViewModel() {

    companion object {
        const val GENRE_COUNT = 6
    }

    val genreViews: MutableLiveData<List<GenreView>> by lazy { MutableLiveData<List<GenreView>>() }

    internal fun loadGenres() {
        Timber.v("loadGenres: IN")
        launchUseCases {
            val genres: List<Genre> = getGenresUseCase()
            genreViews.postValue(genres.map { it.toGenreView() })
        }
        Timber.v("loadGenres: OUT")
    }
}