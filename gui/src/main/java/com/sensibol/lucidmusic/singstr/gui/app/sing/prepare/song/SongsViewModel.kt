package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.song

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.domain.model.Song
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.usecase.GetSongPreviewUrlUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class SongsViewModel @Inject constructor(
    private val getSongUseCase: GetSongsUseCase,
    private val getSongPreviewUrlUseCase: GetSongPreviewUrlUseCase
) : BaseViewModel() {

    private val _songs: MutableLiveData<List<SongMini>> by lazy { MutableLiveData() }
    internal val songs: LiveData<List<SongMini>> = _songs

    internal var genre: Genre? = null
    internal var difficulty: Song.Difficulty? = null
    internal var query: String? = null

    internal fun loadSongs(genre: Genre?, difficulty: Song.Difficulty?, query: String?) {
        Timber.d("loadSongs: genre=$genre")
        this.genre = genre
        this.difficulty = difficulty
        this.query = query
        launchUseCases { _songs.postValue(getSongUseCase(genre, difficulty, query)) }
    }

    private val _previewUrl: MutableLiveData<String> by lazy { MutableLiveData() }
    internal val previewUrl: LiveData<String> = _previewUrl

    internal fun loadSongPreviewUrl(songId: String) {
        launchUseCases {
            _previewUrl.postValue(getSongPreviewUrlUseCase(songId))
        }
    }

}