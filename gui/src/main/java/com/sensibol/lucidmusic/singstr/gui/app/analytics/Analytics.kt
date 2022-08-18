package com.sensibol.lucidmusic.singstr.gui.app.analytics

import android.os.Bundle
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.sensibol.lucidmusic.singstr.domain.model.User
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.utils.Gender
import java.util.*

internal object Analytics {

    internal sealed class Event(val eventName: String) {

        class AppClearDataEvent() : Event("app_clear_data")
        class AppExceptionEvent() : Event("app_exception")
        class AppRemoveEvent() : Event("app_remove")
        class AppStoreRefundEvent() : Event("app_store_refund")
        class SignUpEvent() : Event("sign_up")
        class LoginEvent(private val method: Param.Method) : Event("login") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    method.addToBundle(bundle)
                }
        }

        class SingSongButtonEvent(
            private val userId: Param.UserId
        ) : Event("sing_song_button") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class GenreSelectionEvent(
            private val genreId: Param.GenreId
        ) : Event("genre_selection") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    genreId.addToBundle(bundle)
                }
        }

        class CompletedCoverEvent(
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val songId: Param.SongId,
            private val score: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timingScore: Param.TimeScore,
            private val rightLines: Param.RightLines,
            private val wrongLines: Param.WrongLines
        ) : Event("completed_cover") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    songId.addToBundle(bundle)
                    score.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timingScore.addToBundle(bundle)
                    rightLines.addToBundle(bundle)
                    wrongLines.addToBundle(bundle)
                }

        }

        class CompletedPracticeEvent(
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val songId: Param.SongId,
            private val score: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timingScore: Param.TimeScore,
        ) : Event("completed_practice") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    songId.addToBundle(bundle)
                    score.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timingScore.addToBundle(bundle)
                }
        }

        class LessonViewEvent(
            private val lessonId: Param.LessonId,
            private val lessonOwner: Param.LessonOwner,
            private val lessonCategory: Param.LessonCategory,
            private val lessonCollection: Param.LessonCollection,
            private val lessonStatus: Param.LessonStatus
        ) : Event("lesson_view") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    lessonId.addToBundle(bundle)
                    lessonOwner.addToBundle(bundle)
                    lessonCategory.addToBundle(bundle)
                    lessonCollection.addToBundle(bundle)
                    lessonStatus.addToBundle(bundle)
                }
        }

        class HomePageViewEvent(
            private val scorePercent: Param.ScrollPercent,
        ) : Event("home_page_view") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    scorePercent.addToBundle(bundle)
                }
        }

        class CoverScoreCardShareEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val avgTotalScore: Param.TotalScore,
            private val sharedOn: Param.SharedOnApp
        ) : Event("cover_score_card_share") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    avgTotalScore.addToBundle(bundle)
                    sharedOn.addToBundle(bundle)
                }
        }


        class PublishedCoverEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val avgTotalScore: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timeScore: Param.TimeScore,
            private val rightLines: Param.RightLines,
            private val wrongLines: Param.WrongLines,
            private val coverId: Param.CoverId
        ) : Event("published_cover") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    avgTotalScore.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timeScore.addToBundle(bundle)
                    rightLines.addToBundle(bundle)
                    wrongLines.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                }
        }

        class AcademyPageViewEvent(
            private val scorePercent: Param.ScrollPercent,
        ) : Event("academy_page_view") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    scorePercent.addToBundle(bundle)
                }
        }

        class SearchPageViewEvent(
            private val scorePercent: Param.ScrollPercent,
        ) : Event("search_page_view") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    scorePercent.addToBundle(bundle)
                }
        }

        class NotificationPageViewEvent(
            private val scorePercent: Param.ScrollPercent,
        ) : Event("notification_page_view") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    scorePercent.addToBundle(bundle)
                }
        }

        class TuningEnhanceEvent() : Event("tuning_enhance")
        class TimingEnhanceEvent() : Event("timing_enhance")

        class SingCtaClickEvent(
            private val songId: Param.SongId,
        ) : Event("sing_cta_click") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                }
        }

        class PracticeSessionEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
        ) : Event("practice_session") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                }

        }

        class PostCommentEvent(
            private val songId: Param.SongId,
            private val coverId: Param.CoverId,
            private val userId: Param.UserId
        ) : Event("post_comment") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                    userId.addToBundle(bundle)
                }
        }

        class ClapReactEvent(
            private val songId: Param.SongId,
            private val artistId: Param.ArtistId,
            private val userId: Param.UserId,
            private val coverId: Param.CoverId

        ) : Event("clap_react") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    userId.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                }
        }

        class LearnEarnXpEvent(
            private val userId: Param.UserId
        ) : Event("learn_earn_xp") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class LessonCompleteEvent(
            private val lessonId: Param.LessonId,
            private val lessonOwner: Param.LessonOwner,
            private val lessonCategory: Param.LessonCategory,
            private val lessonCollection: Param.LessonCollection,
            private val lessonStatus: Param.LessonStatus,
            private val viewTime: Param.ViewTime,
            private val viewPercent: Param.ViewPercent
        ) : Event("lesson_complete") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    lessonId.addToBundle(bundle)
                    lessonOwner.addToBundle(bundle)
                    lessonCategory.addToBundle(bundle)
                    lessonCollection.addToBundle(bundle)
                    lessonStatus.addToBundle(bundle)
                    viewTime.addToBundle(bundle)
                    viewPercent.addToBundle(bundle)
                }
        }

        class MCQAttemptEvent(
            private val mcqQuestion: Param.McqQuestion,
            private val mcqAnswer: Param.McqAnswer

        ) : Event("mcq_attempt") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    mcqQuestion.addToBundle(bundle)
                    mcqAnswer.addToBundle(bundle)
                }
        }

        class AddToWatchListEvent(
            private val lessonId: Param.LessonId,
            private val lessonOwner: Param.LessonOwner,
            private val lessonCategory: Param.LessonCategory,
            private val lessonCollection: Param.LessonCollection,
            private val lessonStatus: Param.LessonStatus
        ) : Event("add_to_watchlist") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    lessonId.addToBundle(bundle)
                    lessonOwner.addToBundle(bundle)
                    lessonCategory.addToBundle(bundle)
                    lessonCollection.addToBundle(bundle)
                    lessonStatus.addToBundle(bundle)
                }
        }


        class ExerciseAttemptEvent(
            private val lessonId: Param.LessonId,
            private val lessonOwner: Param.LessonOwner,
            private val lessonCategory: Param.LessonCategory,
            private val lessonStatus: Param.LessonStatus,
            private val exerciseId: Param.ExerciseId,
        ) : Event("attempt_exercise") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    lessonId.addToBundle(bundle)
                    lessonOwner.addToBundle(bundle)
                    lessonCategory.addToBundle(bundle)
                    lessonStatus.addToBundle(bundle)
                    exerciseId.addToBundle(bundle)
                }
        }

        class ExerciseReAttemptEvent(
            private val lessonId: Param.LessonId,
            private val lessonOwner: Param.LessonOwner,
            private val lessonCategory: Param.LessonCategory,
            private val lessonStatus: Param.LessonStatus,
            private val exerciseId: Param.ExerciseId,
        ) : Event("re_attempt_exercise") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    lessonId.addToBundle(bundle)
                    lessonOwner.addToBundle(bundle)
                    lessonCategory.addToBundle(bundle)
                    lessonStatus.addToBundle(bundle)
                    exerciseId.addToBundle(bundle)
                }
        }

        class ExerciseCompleteEvent(
            private val lessonId: Param.LessonId,
            private val lessonOwner: Param.LessonOwner,
            private val lessonCategory: Param.LessonCategory,
            private val lessonStatus: Param.LessonStatus,
            private val exerciseId: Param.ExerciseId,
            private val tuneScore: Param.TuneScore,
            private val timeScore: Param.TimeScore
        ) : Event("complete_exercise") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    lessonId.addToBundle(bundle)
                    lessonOwner.addToBundle(bundle)
                    lessonCategory.addToBundle(bundle)
                    lessonStatus.addToBundle(bundle)
                    exerciseId.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timeScore.addToBundle(bundle)
                }
        }

        class CheckProDetailsEvent(
            private val proUserId: Param.ProUserId
        ) : Event("check_pro_details") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    proUserId.addToBundle(bundle)
                }
        }

        class DailyChallengeAttemptEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
        ) : Event("daily_challenge_attempt") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                }
        }

        class CoverSettingEvent(
            private val optionSelect: Param.OptionSelect
        ) : Event("cover_setting") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    optionSelect.addToBundle(bundle)
                }
        }

        class CheckLeaderboardEvent(
            private val userRank: Param.UserRank
        ) : Event("check_leaderboard") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userRank.addToBundle(bundle)
                }
        }

        class DeleteCoverEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
        ) : Event("delete_cover") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                }
        }

        class DetailedAnalysisEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val avgTotalScore: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timeScore: Param.TimeScore,
            private val rightLines: Param.RightLines,
            private val wrongLines: Param.WrongLines

        ) : Event("detailed_analysis_cta") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    avgTotalScore.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timeScore.addToBundle(bundle)
                    rightLines.addToBundle(bundle)
                    wrongLines.addToBundle(bundle)
                }
        }

        class CheckDetailedAnalysisEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val coverId: Param.CoverId,
            private val rightLines: Param.RightLines,
            private val wrongLines: Param.WrongLines,
            private val avgTotalScore: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timeScore: Param.TimeScore,

            ) : Event("check_detailed_analysis") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                    rightLines.addToBundle(bundle)
                    wrongLines.addToBundle(bundle)
                    avgTotalScore.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timeScore.addToBundle(bundle)
                }
        }

        class CheckRewardsEvent(
            private val userId: Param.UserId
        ) : Event("check_rewards"){
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class ProfileViewEvent(
            private val userId: Param.UserId
        ) : Event("Profile_Page_view") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class MessageViewEvent() : Event("Message_Page_view")

        class SkippedWalkThroughEvent(
            private val screenName: Param.SkippedScreenName
        ) :
            Event("skipped_walkthrough") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    screenName.addToBundle((bundle))
                }
        }

        class CoverAttemptEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
        ) : Event("attempt_cover") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                }
        }

        class SwitchCameraEvent(
            private val cameraFacing: Param.CameraFacing,
        ) : Event("switch_rear_camera") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    cameraFacing.addToBundle(bundle)
                }
        }

        class RestartCoverEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
        ) : Event("restart_cover") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                }
        }

        class KeyChangeEvent() : Event("key_change")

        class GuideVocalsEvent() : Event("guide_vocal")

        class ChangeThumbnailEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val avgTotalScore: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timeScore: Param.TimeScore,
            private val coverId: Param.CoverId
        ) : Event("change_thumbnail"){
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    avgTotalScore.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timeScore.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                }
        }

        class TryAgainCoverEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val avgTotalScore: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timeScore: Param.TimeScore
        ) : Event("try_again_cover") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    avgTotalScore.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timeScore.addToBundle(bundle)
                }
        }

        class FilterDetailedAnalysisEvent(
            private val songId: Param.SongId,
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val coverId: Param.CoverId,
            private val filterState: Param.FilterState,
            private val avgTotalScore: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timeScore: Param.TimeScore
        ) : Event("filter_detailed_analysis") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                    filterState.addToBundle(bundle)
                    avgTotalScore.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timeScore.addToBundle(bundle)
                }
        }

        class DetailedAnalysisInteractionEvent(
            private val analysisClick: Param.AnalysisClick,
            private val rightLines: Param.RightLines,
            private val wrongLines: Param.WrongLines,
            private val coverId: Param.CoverId
        ) : Event("detailed_analysis_interaction") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    analysisClick.addToBundle(bundle)
                    rightLines.addToBundle(bundle)
                    wrongLines.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                }
        }

        class CheckLessonDetailsEvent(
            private val lessonId: Param.LessonId,
            private val lessonOwner: Param.LessonOwner,
            private val lessonCategory: Param.LessonCategory,
            private val lessonCollection: Param.LessonCollection,
            private val lessonStatus: Param.LessonStatus
        ) : Event("check_lesson_details") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    lessonId.addToBundle(bundle)
                    lessonOwner.addToBundle(bundle)
                    lessonCategory.addToBundle(bundle)
                    lessonCollection.addToBundle(bundle)
                    lessonStatus.addToBundle(bundle)
                }
        }

        class CheckMentorProfileEvent(
            private val mentorName: Param.MentorName,
        ) : Event("check_mentor_profile") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    mentorName.addToBundle(bundle)
                }
        }

        class FollowMentorEvent(
            private val mentorName: Param.MentorName,
        ) : Event("follow_mentor") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    mentorName.addToBundle(bundle)
                }
        }

        class ShareLessonEvent(
            private val lessonId: Param.LessonId,
            private val lessonOwner: Param.LessonOwner,
            private val lessonCategory: Param.LessonCategory,
            private val lessonCollection: Param.LessonCollection,
            private val lessonStatus: Param.LessonStatus
        ) : Event("share_lesson") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    lessonId.addToBundle(bundle)
                    lessonOwner.addToBundle(bundle)
                    lessonCategory.addToBundle(bundle)
                    lessonCollection.addToBundle(bundle)
                    lessonStatus.addToBundle(bundle)
                }
        }

        class ShareLessonGroupEvent(
            private val lessonGroupId: Param.LessonGroupId,
            private val lessonGroupName: Param.LessonGroupName,
            ) : Event("share_lesson_group") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    lessonGroupId.addToBundle(bundle)
                    lessonGroupName.addToBundle(bundle)
                }
        }

        class ViewCoverEvent(
            private val songId: Param.SongId,
            private val artistId: Param.ArtistId,
            private val coverId: Param.CoverId,
            private val userId: Param.UserId,
            private val totalViews: Param.TotalViews,
            private val totalClaps: Param.TotalClaps,
        ) : Event("view_cover") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                    userId.addToBundle(bundle)
                    totalViews.addToBundle(bundle)
                    totalClaps.addToBundle(bundle)
                }
        }

        class ViewOwnCoverEvent(
            private val songId: Param.SongId,
            private val artistId: Param.ArtistId,
            private val coverId: Param.CoverId,
            private val userId: Param.UserId,
        ) : Event("view_own_cover") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                    userId.addToBundle(bundle)
                }
        }

        class ShareCoverEvent(
            private val songId: Param.SongId,
            private val artistId: Param.ArtistId,
            private val coverId: Param.CoverId,
            private val userId: Param.UserId,
        ) : Event("share_cover") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                    userId.addToBundle(bundle)
                }
        }

        class ReportContentEvent(
            private val coverId: Param.CoverId,
            private val userId: Param.UserId,
        ) : Event("report_content") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    coverId.addToBundle(bundle)
                    userId.addToBundle(bundle)
                }
        }

        class ReportUserEvent(
            private val coverId: Param.CoverId,
            private val userId: Param.UserId,
        ) : Event("report_user") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    coverId.addToBundle(bundle)
                    userId.addToBundle(bundle)
                }
        }

        class BlockContentEvent() : Event("block_content")

        class HomeLearnButtonEvent(
            private val userId: Param.UserId
        ) : Event("home_learn_button") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }


        class HeroBannerClickEvent(
            private val bannerId: Param.BannerId
        ) : Event("hero_banner_click") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    bannerId.addToBundle(bundle)
                }
        }

        class ViewAppFeaturesEvent(
            private val userId: Param.UserId
        ) : Event("view_app_features"){
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class ViewAllLessonsEvent(
            private val userId: Param.UserId
        ) : Event("view_all_lessons"){
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class ViewAllCoversEvent(
            private val userId: Param.UserId
        ) : Event("view_all_covers"){
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class HomePracticeCTAEvent(
            private val userId: Param.UserId
        ) : Event("home_practice_cta"){
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class SelectProPlanEvent(
            private val planDetail: Param.PlanDetail
        ) : Event("select_pro_plan") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    planDetail.addToBundle(bundle)
                }
        }

        class ProPurchaseEvent(
            private val planDetail: Param.PlanDetail,
            private val planValue: Param.PlanValue,
        ) : Event("pro_purchase") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    planDetail.addToBundle(bundle)
                    planValue.addToBundle(bundle)
                }
        }

        class UserProfileViewEvent(
            private val userId: Param.UserId,
        ) : Event("user_profile_view") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class SubscribeUserEvent(
            private val userId: Param.UserId,
        ) : Event("subscribe_user") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class ShareUserProfileEvent(
            private val userId: Param.UserId,
        ) : Event("share_user_profile") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class EditProfileEvent(
            private val userId: Param.UserId
        ) : Event("edit_profile") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class SaveProfileEvent(
            private val userId: Param.UserId
        ) : Event("save_profile") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class ViewSubscribersEvent() : Event("view_subscribers")

        class ProfileProCTAEvent(
            private val userId: Param.UserId
        ) : Event("profile_pro_cta") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class SearchQueryEvent(
            private val searchQuery: Param.SearchQuery,
        ) : Event("search_query") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    searchQuery.addToBundle(bundle)
                }
        }

        class SearchFilterClickEvent(
            private val filterState: Param.FilterState,
        ) : Event("search_filter_click") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    filterState.addToBundle(bundle)
                }
        }

        class ViewContentEvent(
            private val songId: Param.SongId,
            private val artistId: Param.ArtistId,
            private val coverId: Param.CoverId,
            private val userId: Param.UserId,
            private val totalViews: Param.TotalViews,
            private val totalClaps: Param.TotalClaps,
        ) : Event("fb_mobile_add_payment_info") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    songId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    coverId.addToBundle(bundle)
                    userId.addToBundle(bundle)
                    totalViews.addToBundle(bundle)
                    totalClaps.addToBundle(bundle)
                }
        }

        class AchieveLevelEvent(
            private val userLevel: Int
        ) : Event("achieve_level") {

        }

        class CompleteTutorialEvent(
            private val lessonId: Param.LessonId,
            private val lessonOwner: Param.LessonOwner,
            private val lessonCategory: Param.LessonCategory,
            private val lessonCollection: Param.LessonCollection,
            private val lessonStatus: Param.LessonStatus
        ) : Event("fb_mobile_tutorial_completion") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    lessonId.addToBundle(bundle)
                    lessonOwner.addToBundle(bundle)
                    lessonCategory.addToBundle(bundle)
                    lessonCollection.addToBundle(bundle)
                    lessonStatus.addToBundle(bundle)
                }
        }

        class SubmitApplicationEvent(
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val songId: Param.SongId,
            private val score: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timingScore: Param.TimeScore,
        ) : Event("fb_mobile_add_to_cart") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    songId.addToBundle(bundle)
                    score.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timingScore.addToBundle(bundle)
                }
        }

        class UnlockAchievementEvent(
            private val earnXPValue: Param.EarnXPValue,
            private val earnXPAction: Param.EarnXPAction
        ) : Event("fb_mobile_achievement_unlocked") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    earnXPValue.addToBundle(bundle)
                    earnXPAction.addToBundle(bundle)
                }
        }

        class CompleteRegistrationEvent(
            private val scorePercent: Param.ScrollPercent,
        ) : Event("complete_registration") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    scorePercent.addToBundle(bundle)
                }
        }

        class CompleteRegistrationNewEvent(
            private val registrationMethod: Param.RegistrationMethod,
        ) : Event("fb_mobile_completed_registration") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    registrationMethod.addToBundle(bundle)
                }
        }

        class NotificationClickEvent(
            private val notificationCategory: Param.NotificationCategory
        ) : Event("ss_notification_click") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    notificationCategory.addToBundle(bundle)
                }
        }

        class EarnedXPEvent(
            private val earnXPValue: Param.EarnXPValue,
            private val earnXPAction: Param.EarnXPAction
        ) : Event("earned_xp") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    earnXPValue.addToBundle(bundle)
                    earnXPAction.addToBundle(bundle)
                }
        }

        class FbMobileLevelAchievedEvent(
            private val userLevel: Param.UserLevel,
        ) : Event("fb_mobile_level_achieved") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userLevel.addToBundle(bundle)
                }
        }

        class CheckDraftsEvent(
            private val userId: Param.UserId
        ) : Event("check_drafts"){
            override val params: Bundle
                get() = super.params.also { bundle ->
                    userId.addToBundle(bundle)
                }
        }

        class DraftPublishEvent(
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val songId: Param.SongId,
            private val score: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timingScore: Param.TimeScore,
            private val xpGain: Param.XPGain,
            private val rightLines: Param.RightLines,
            private val wrongLines: Param.WrongLines,
        ) : Event("draft_publish") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    songId.addToBundle(bundle)
                    score.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timingScore.addToBundle(bundle)
                    xpGain.addToBundle(bundle)
                    rightLines.addToBundle(bundle)
                    wrongLines.addToBundle(bundle)
                }
        }

        class DraftAnalyseEvent(
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val songId: Param.SongId,
            private val score: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timingScore: Param.TimeScore,
            private val xpGain: Param.XPGain,
            private val rightLines: Param.RightLines,
            private val wrongLines: Param.WrongLines
        ) : Event("draft_analyse") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    songId.addToBundle(bundle)
                    score.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timingScore.addToBundle(bundle)
                    xpGain.addToBundle(bundle)
                    rightLines.addToBundle(bundle)
                    wrongLines.addToBundle(bundle)
                }
        }

        class DraftDeleteEvent(
            private val genreId: Param.GenreId,
            private val artistId: Param.ArtistId,
            private val songId: Param.SongId,
            private val score: Param.TotalScore,
            private val tuneScore: Param.TuneScore,
            private val timingScore: Param.TimeScore,
            private val xpGain: Param.XPGain
        ) : Event("draft_delete") {
            override val params: Bundle
                get() = super.params.also { bundle ->
                    genreId.addToBundle(bundle)
                    artistId.addToBundle(bundle)
                    songId.addToBundle(bundle)
                    score.addToBundle(bundle)
                    tuneScore.addToBundle(bundle)
                    timingScore.addToBundle(bundle)
                    xpGain.addToBundle(bundle)
                }
        }


        internal open val params: Bundle get() = Bundle()


        internal sealed class Param(val name: String) {

            abstract fun addToBundle(bundle: Bundle)

            internal class GenreId(val value: String) : Param("genre_id") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class SongId(val value: String) : Param("song_id") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class ArtistId(val value: String) : Param("artist_id") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class LessonId(val value: String) : Param("lesson_id") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class LessonGroupId(val value: String) : Param("lesson_group_id") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class LessonGroupName(val value: String) : Param("lesson_group_name") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class ExerciseId(val value: String) : Param("exercise_id") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }


            internal class LessonOwner(val value: String) : Param("lesson_owner") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }


            internal class LessonCategory(val value: String) : Param("lesson_category") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class LessonCollection(val value: String) : Param("lesson_collection") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class LessonStatus(val value: String) : Param("lesson_status") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class ScrollPercent(val value: String) : Param("scroll_percent") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class Difficulty(val value: String) : Param("difficulty") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class Method(val value: String) : Param("method") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class McqAnswer(val value: Boolean) : Param("answer") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putBoolean(name, value)
                }
            }

            internal class McqQuestion(val value: String) : Param("question") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class OptionSelect(val value: String) : Param("option_select") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class TotalScore(val value: Int) : Param("avg_total_score") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putInt(name, value)
                }
            }

            internal class TuneScore(val value: Int) : Param("tune_score") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putInt(name, value)
                }
            }

            internal class TimeScore(val value: Int) : Param("time_score") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putInt(name, value)
                }
            }

            internal class UserRank(val value: Int) : Param("user_rank") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putInt(name, value)
                }
            }

            internal class ProUserId(val value: String) : Param("pro_user_id") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class SkippedScreenName(val value: String) : Param("screen_name") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class CameraFacing(val value: String) : Param("camera_facing") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class CoverId(val value: String) : Param("cover_id") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class MentorName(val value: String) : Param("mentor_name") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class TotalViews(val value: String) : Param("total_views") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class TotalClaps(val value: String) : Param("total_claps") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class UserId(val value: String) : Param("ss_user_id") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class BannerId(val value: String) : Param("banner_id") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class PlanDetail(val value: String) : Param("plan_detail") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class PlanValue(val value: String) : Param("plan_value") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class AnalysisClick(val value: String) : Param("analysis_click") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class RightLines(val value: Int) : Param("right_lines") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putInt(name, value)
                }
            }

            internal class WrongLines(val value: Int) : Param("wrong_lines") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putInt(name, value)
                }
            }

            internal class NotificationCategory(val value: String) : Param("notification_category") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class ViewTime(val value: Int) : Param("view_time") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putInt(name, value)
                }
            }

            internal class ViewPercent(val value: Int) : Param("view_percent") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putInt(name, value)
                }
            }

            internal class UserLevel(val value: String) : Param("LEVEL") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class EarnXPValue(val value: Int) : Param("xp_value") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putInt(name, value)
                }
            }

            internal class XPGain(val value: Int) : Param("xp_gain") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putInt(name, value)
                }
            }

            internal class EarnXPAction(val value: String) : Param("xp_action") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class RegistrationMethod(val value: String) : Param("registration_method") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class FilterState(val value: String) : Param("filter_state") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class SearchQuery(val value: String) : Param("query") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

            internal class SharedOnApp(val value: String) : Param("shared_on_app") {
                override fun addToBundle(bundle: Bundle) {
                    bundle.putString(name, value)
                }
            }

        }

    }

    internal fun logEvent(event: Event) {
        Firebase.analytics.logEvent(event.eventName, event.params)
        facebookLogEvent(event)

        val eventParams: MutableMap<String, Any> = mutableMapOf()
        event.params.keySet().forEach { key -> eventParams[key] = event.params[key] as Any }
        WebEngage.get().analytics().track(event.eventName, eventParams)

    }

    internal fun facebookLogEvent(event: Event) {
        AppEventsLogger
            .newLogger(getApplicationContext())
            .logEvent(event.eventName, event.params)
    }

    internal sealed class UserProperty(val name: String, open val value: Any) {
        internal class UserXp(userXp: Int) : UserProperty("ss_user_xp", userXp)
        internal class UserLevel(userLevel: Int) : UserProperty("ss_user_level", userLevel)
        internal class UserGender(userGender: String) : UserProperty("ss_user_gender", userGender)
        internal class UserBirthDate(userBirthDate: Date) : UserProperty("ss_user_birth_date", userBirthDate)
        internal class UserPlan(userPlan: String) : UserProperty("ss_user_plan", userPlan)
        internal class UserRevenue(userRevenue: String) : UserProperty("ss_user_revenue", userRevenue)
        internal class UserRenewalDate(userRenewalDate: Date) : UserProperty("ss_user_renewal", userRenewalDate)
        internal class UserContentViewTime(contentViewTime: Date) : UserProperty("content_view_time", contentViewTime)
        internal class UserName(userName: String) : UserProperty("ss_user_name", userName)
        internal class UserLocation(userLocation: String) : UserProperty("ss_user_location", userLocation)
        internal class UserPrefLanguage(userPrefLanguage: HashSet<String>) : UserProperty(
            "ss_user_pref_language",
            mutableListOf(userPrefLanguage).toString(),
        )

        internal class UserSubscribers(userSubscribers: Int) : UserProperty("subscribers", userSubscribers)

        internal class UserTransactions(userTransactions: String) : UserProperty("ss_user_transactions", userTransactions)
        internal class UserLocalRank(userLocalRank: Int) : UserProperty("ss_user_local_rank", userLocalRank)
        internal class UserGlobalRank(userGlobalRank: Int) : UserProperty("current_user_rank", userGlobalRank)
        internal class PublishedCovers(publishedCovers: Int) : UserProperty("published_covers", publishedCovers)
        internal class DraftCovers(draftsCovers: Int) : UserProperty("draft_covers", draftsCovers)
        internal class AvgTuneScore(avgTuneScore: Int) : UserProperty("avg_tune_score", avgTuneScore)
        internal class AvgTimeScore(avgTimeScore: Int) : UserProperty("avg_time_score", avgTimeScore)

    }

    internal fun setUserProperty(userProperty: UserProperty) {
        Firebase.analytics.setUserProperty(userProperty.name, userProperty.value.toString())
        when (userProperty.value) {
            is Int -> WebEngage.get().user().setAttribute(userProperty.name, userProperty.value as Int)
            is Date -> WebEngage.get().user().setAttribute(userProperty.name, userProperty.value as Date)
            is String -> WebEngage.get().user().setAttribute(userProperty.name, userProperty.value as String)
            is Boolean -> WebEngage.get().user().setAttribute(userProperty.name, userProperty.value as Boolean)
            else -> WebEngage.get().user().setAttribute(userProperty.name, userProperty.value.toString())
        }
    }

    internal fun setWebEngageUserSystemProperty(user: User) {
        WebEngage.get().user().apply {
            login(user.id)
            setEmail(user.email)
            setFirstName(user.name)
            setGender(Gender.valueByString(user.sex))
        }
    }

}