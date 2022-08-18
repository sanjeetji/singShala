package com.sensibol.lucidmusic.singstr.domain.model

import com.sensibol.lucidmusic.singstr.domain.utils.RandomFixture
import com.sensibol.lucidmusic.singstr.domain.utils.UniqueFixture

object CoverFixture {

    fun newCovers(count: Int = 10): List<Attempt> = MutableList(count) { newCover() }

    fun newCover(
        attemptId: String = UniqueFixture.newStringId(),
        attemptTime: String = UniqueFixture.newTimestamp(),
        comment: String = "Nice!",
        coverUrl: String = UniqueFixture.newUrl("cover"),
        publicMediaUrl: String = UniqueFixture.newUrl("media"),
        score: Attempt.Score = newScore(),
        songId: String = UniqueFixture.newStringId(),
        songTitle: String = "Song Title"
    ): Attempt = Attempt(
        id = attemptId,
//        id = attemptTime,
        comment = comment,
        coverUrl = coverUrl,
        publicMediaUrl = publicMediaUrl,
        score = score,
        songId = songId,
        songTitle = songTitle,
    )

    fun newScore(
        detailScore: Attempt.Score.DetailScore = newDetailScore(),
        total: Float = RandomFixture.randomFloat() * 100,
    ): Attempt.Score = Attempt.Score(
        detailScore = detailScore,
        total = total
    )

    fun newDetailScore(
        lessonScore: Float = RandomFixture.randomFloat() * 100,
        reviewData: String = "{}",
        timingScore: Float = RandomFixture.randomFloat() * 100,
        tuneScore: Float = RandomFixture.randomFloat() * 100,
    ): Attempt.Score.DetailScore = Attempt.Score.DetailScore(
        lessonScore = lessonScore,
        reviewData = reviewData,
        timingScore = timingScore,
        tuneScore = tuneScore,
    )
}