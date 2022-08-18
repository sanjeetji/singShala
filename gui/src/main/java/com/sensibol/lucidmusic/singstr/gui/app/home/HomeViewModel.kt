package com.sensibol.lucidmusic.singstr.gui.app.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getNewLessonsUseCase: GetNewLessonsUseCase,
    private val getFeedUseCase: GetFeedUseCase,
    private val getCarouselBannersUseCase: GetCarouselBannersUseCase,
    private val checkStreakUseCase: CheckStreakUseCase,
    private val getStreakInfoUseCase: GetStreakInfoUseCase,
    private val getContestInfoUseCase: GetContestInfoUseCase,
    private val getContestInfoByIdUseCase: GetContestInfoByIdUseCase,
    private val getLearnContentUseCase: GetLearnContentUseCase,
    private val getAllConceptUseCase: GetAllConceptUseCase,
    private val getLeaderBoardUserRankUseCase: GetLeaderBoardUserRankUseCase
) : BaseViewModel() {

    var nextPageToken: String? = null
    val allconectpData = null
    private val _feed1: MutableLiveData<Feed> by lazy { MutableLiveData() }
    internal val feed1: LiveData<Feed> = _feed1

    private val _feed2: MutableLiveData<Feed> by lazy { MutableLiveData() }
    internal val feed2: LiveData<Feed> = _feed2

    private val _feed3: MutableLiveData<Feed> by lazy { MutableLiveData() }
    internal val feed3: LiveData<Feed> = _feed3

    private val _feed4: MutableLiveData<Feed> by lazy { MutableLiveData() }
    internal val feed4: LiveData<Feed> = _feed4

    private val _feed5: MutableLiveData<Feed> by lazy { MutableLiveData() }
    internal val feed5: LiveData<Feed> = _feed5

    private val _feed: MutableLiveData<Feed> by lazy { MutableLiveData() }
    internal val feed: LiveData<Feed> = _feed

    internal fun loadFeed() {
        Timber.d("loadFeed: ")
        launchUseCases {
            val feed1 = getFeedUseCase(null)
            _feed1.postValue(feed1)
            val feed2 = getFeedUseCase(feed1.nextPageToken)
            _feed2.postValue(feed2)
            val feed3 = getFeedUseCase(feed2.nextPageToken)
            _feed3.postValue(feed3)
            val feed4 = getFeedUseCase(feed3.nextPageToken)
            _feed4.postValue(feed4)
            val feed5 = getFeedUseCase(feed4.nextPageToken)
            _feed5.postValue(feed5)
        }
    }

    private val _newLessons: MutableLiveData<List<LessonMini>> by lazy { MutableLiveData() }
    internal val newLessons: LiveData<List<LessonMini>> = _newLessons

    internal fun loadNewLessons() {
        launchUseCases {
            _newLessons.postValue(getNewLessonsUseCase())
        }
    }

    private val _carouselBanners: MutableLiveData<List<CarouselBanner>> by lazy { MutableLiveData() }
    internal val carouselBanners: LiveData<List<CarouselBanner>> = _carouselBanners

    internal fun loadCarouselBanners() {
        launchUseCases {
            _carouselBanners.postValue(getCarouselBannersUseCase())
        }
    }

    private val _streakInfo: MutableLiveData<StreakInfo> by lazy { MutableLiveData() }
    internal val streakInfo: LiveData<StreakInfo> = _streakInfo

    internal fun checkStreak(){
        launchUseCases {
            val checkStreak = checkStreakUseCase.invoke()
            if (checkStreak.data == "Record inserted") {
                val streakInfo = getStreakInfoUseCase.invoke()
                _streakInfo.postValue(streakInfo)
            }
        }
    }

    private val _academyContent: MutableLiveData<AcademyContent> by lazy { MutableLiveData<AcademyContent>() }
    internal val academyContent: LiveData<AcademyContent> = _academyContent

    internal fun loadLearnContent() {
        launchUseCases {
            _academyContent.postValue(getLearnContentUseCase())
        }
    }

    private val _coverLessonData: MutableLiveData<CoverLesson> by lazy { MutableLiveData<CoverLesson>() }
    internal val coverLessonData: LiveData<CoverLesson> = _coverLessonData

    internal fun loadCoverLessonData() {
        launchUseCases {

//            val lessonGroupList = getLearnContentUseCase().lessonGroups

            val allConceptData = getAllConceptUseCase(nextPageToken).also {
                nextPageToken = it.data.nextPageToken
            }

            val feedList = mutableListOf<Feed>()
            var pageToken:String? = null
            for (i in allConceptData.data.conceptList){
                val feed = getFeedUseCase(pageToken)
                pageToken = feed.nextPageToken
                _feed.postValue(feed)
                feedList.add(feed)
            }
            _coverLessonData.postValue(CoverLesson(feedList,allConceptData.data.conceptList))
        }
    }


    internal fun getContestInfo(){
        launchUseCases {
            val contestInfo = getContestInfoUseCase.invoke()
            if (contestInfo.get(0).hostName == "Lorem Ipsum"){

            }
        }
    }

    internal fun getContestInfoById(){
        launchUseCases {
            val contestInfo = getContestInfoByIdUseCase.invoke()
            if (contestInfo.hostName == "Lorem Ipsum"){

            }
        }
    }

    private val _leaderUserRank: MutableLiveData<Int> by lazy { MutableLiveData() }
    internal val leaderUserRank: LiveData<Int> = _leaderUserRank

    fun getUserRank() {
        launchUseCases {
            _leaderUserRank.postValue(getLeaderBoardUserRankUseCase())
        }
    }


}