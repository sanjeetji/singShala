package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.Exercise
import com.sensibol.lucidmusic.singstr.domain.model.Lesson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


internal fun LessonResponse.toLesson() = Lesson(
    displayOrder = displayOrder,
    id = id,
    title = displayName,
    type = primaryTag,
    isAddedToMyList = isAddedToMyList,
    difficulty = difficulty,
    subscriptionType = subscriptionType,
    duration = videoInfo.duration,
    thumbnailUrl = videoInfo.thumbnailUrl,
    videoUrl = videoInfo.videoUrl,
    description = description,
    exercises = if (exercises.isNullOrEmpty()) {
        listOf<Exercise>()
    } else
        exercises.map { it.toExercise() },

    relatedSongs = relatedSongs.songs.map { it.toSongMini() },
    teacher = teacher.toTeacher(),
    nextLesson = nextLesson.toLessonMini(),
)

@JsonClass(generateAdapter = true)
internal class LessonResponse(

    @Json(name = "order")
    val displayOrder: Int,

    @Json(name = "id")
    val id: String,

    @Json(name = "display_name")
    val displayName: String,

    @Json(name = "difficulty")
    val difficulty: String,

    @Json(name = "primary_tag")
    val primaryTag: String,

    @Json(name = "is_added_to_my_list")
    val isAddedToMyList: Boolean,

    @Json(name = "subscription_purchase_type")
    val subscriptionType: String,

    @Json(name = "video")
    val videoInfo: VideoInfoResponse,

    @Json(name = "description")
    val description: String,

    @Json(name = "exercises")
    val exercises: List<ExerciseResponse>?,

    @Json(name = "related_songs")
    val relatedSongs: RelatedSongResponse,

    @Json(name = "teacher")
    val teacher: TeacherResponse,

    @Json(name = "next_lesson")
    val nextLesson: LessonMiniResponse,
) {

    @JsonClass(generateAdapter = true)
    class RelatedSongResponse(

        @Json(name = "songs")
        val songs: List<SongMiniResponse>
    )
}

