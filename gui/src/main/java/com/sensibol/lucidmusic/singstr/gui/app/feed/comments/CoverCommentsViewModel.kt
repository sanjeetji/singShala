package com.sensibol.lucidmusic.singstr.gui.app.feed.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.AllReplyComment
import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.domain.model.SearchData
import com.sensibol.lucidmusic.singstr.gui.app.search.SearchNode
import com.sensibol.lucidmusic.singstr.gui.app.search.SearchPageSource
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class CoverCommentsViewModel @Inject constructor(
    private val addFeedCommentUseCase: AddFeedCommentUseCase,
    private val getCoverCommentsUseCase: GetCoverCommentsUseCase,
    private val addFeedCommentReplyUseCase: AddFeedCommentReplyUseCase,
    private val getFeedCommentReplyUseCase: GetFeedCommentReplyUseCase,
    private val addFeedCommentOnCoverUseCase: AddFeedCommentOnCoverUseCase,
    private val getSearchUseCase: GetSearchUseCase
) : BaseViewModel() {

    private val _comments: MutableLiveData<List<Comment>> by lazy { MutableLiveData() }
    internal val comments: LiveData<List<Comment>> = _comments

    internal fun loadCoverComments(coverId: String) {
        launchUseCases {
            _comments.postValue(getCoverCommentsUseCase(coverId))
        }
    }

    private val _addComment: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val addComment: LiveData<Boolean> = _addComment

    internal fun loadAddFeedComments(attemptId: String, comment: String) {
        launchUseCases {
            _addComment.postValue(addFeedCommentUseCase(attemptId, comment))
        }
    }

    private val _addCommentReply: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    internal val addCommentReply: LiveData<String> = _addCommentReply

    internal fun loadAddFeedCommentReply(attemptId: String,commentId:String, reply: String,tagUserId:List<String>) {
        launchUseCases {
            _addCommentReply.postValue(addFeedCommentReplyUseCase(attemptId,commentId,reply,tagUserId))
        }
    }

    private val _getCommentReply: MutableLiveData<AllReplyComment> by lazy { MutableLiveData<AllReplyComment>() }
    internal val getCommentReply: LiveData<AllReplyComment> = _getCommentReply

    internal fun loadFeedCommentReply(commentId:String) {
        launchUseCases {
            _getCommentReply.postValue(getFeedCommentReplyUseCase(commentId))
        }
    }

    private val _getUserNames: MutableLiveData<SearchData> by lazy { MutableLiveData<SearchData>() }
    internal val getUserNames: LiveData<SearchData> = _getUserNames

    internal fun loadUserName(keyword: String, lookup: String) {
        launchUseCases {
            _getUserNames.postValue(getSearchUseCase(keyword,lookup,""))
        }
    }

    private val _addCommentOnCover: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    internal val addCommentOnCover: LiveData<String> = _addCommentOnCover

    internal fun loadAddFeedCommentOnCover(comment:String, attemptId: String,tagUserId:List<String>) {
        launchUseCases {
            _addCommentOnCover.postValue(addFeedCommentOnCoverUseCase(comment, attemptId,tagUserId))
        }
    }
}