package com.sensibol.lucidmusic.singstr.gui.app.profile.self.drafts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class CoverDraftsViewModel @Inject constructor(
    private val getCoverDraftsUseCase: GetCoverDraftsUseCase,
    private val deleteDraftUseCase: DeleteDraftUseCase,
    private val getSimpleAnalysisUseCase: GetSimpleAnalysisUseCase,
    private val getNodeDraftUseCase: GetNodeDraftUseCase,
    private val getSongUseCase: GetSongUseCase
) : BaseViewModel() {

    private val _DraftsPage: MutableLiveData<DraftsPage> by lazy { MutableLiveData<DraftsPage>() }
    internal val draftsPage: LiveData<DraftsPage> = _DraftsPage

    internal fun getDrafts() {
        launchUseCases {
            _DraftsPage.postValue(getCoverDraftsUseCase(null))
        }
    }

    internal fun deleteDraft(attemptId: String) {
        launchUseCases {
            deleteDraftUseCase(attemptId)
            // FIXME - possibly pagination logic would fail
            _DraftsPage.postValue(getCoverDraftsUseCase(null))
        }
    }

    private val _songMini: MutableLiveData<SongMini> by lazy { MutableLiveData() }
    internal val songMini: LiveData<SongMini> = _songMini

    private val _simpleAnalysis: MutableLiveData<SimpleAnalysis> by lazy { MutableLiveData() }
    internal val simpleAnalysis: LiveData<SimpleAnalysis> = _simpleAnalysis

    internal fun loadSimpleAnalysis(attemptId: String, songId: String) {
        Timber.d("loadSimpleAnalysis: IN ")
        launchUseCases {
            val song = getSongUseCase(songId)
            val songMini = SongMini(
                song.id,
                0,
                song.title,
                song.artists,
                song.difficulty,
                song.thumbnailUrl,
                song.isPracticable,
                song.lyricsStartTimeMS,
                song.lyrics
            )
            _songMini.postValue(songMini)
            _simpleAnalysis.postValue(getSimpleAnalysisUseCase(attemptId))
        }
    }



    private val _nodeDraft: MutableLiveData<List<NodeDraft>> by lazy { MutableLiveData() }
    internal val nodeDraft: LiveData<List<NodeDraft>> = _nodeDraft

    internal fun loadNodeDraft(userId: String) {
        launchUseCases {
            _nodeDraft.postValue(getNodeDraftUseCase(userId))
        }
    }


}