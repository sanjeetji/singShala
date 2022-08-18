package com.sensibol.lucidmusic.singstr.gui.app.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.usecase.GetGenresUseCase
import com.sensibol.lucidmusic.singstr.usecase.SetUserSelectedSongGenreUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OnBoardGenreViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase,
    private val setUserSelectedSongGenreUserCase: SetUserSelectedSongGenreUserCase
) : BaseViewModel() {

    private val _genres: MutableLiveData<List<Genre>> by lazy { MutableLiveData() }
    internal val genres: LiveData<List<Genre>> = _genres

    internal fun loadGenres() {
        launchUseCases {
            _genres.postValue(getGenresUseCase())
        }
    }

    private val _addUserPreference: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    internal val addUserPreference: LiveData<Boolean> = _addUserPreference

    fun addUserPreference(selectedList: List<String>?, language: List<String>?) {
        launchUseCases {
            _addUserPreference.postValue(setUserSelectedSongGenreUserCase(selectedList, language))
        }
    }
}