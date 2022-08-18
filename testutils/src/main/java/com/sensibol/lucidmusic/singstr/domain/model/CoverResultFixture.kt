package com.sensibol.lucidmusic.singstr.domain.model

import com.sensibol.lucidmusic.singstr.domain.utils.RandomFixture
import com.sensibol.lucidmusic.singstr.domain.utils.UniqueFixture

object CoverResultFixture {

    fun newCoverResult(
        songId: String = UniqueFixture.newStringId(),
        songDurationMS: Int = RandomFixture.randomInt(30_000, 100_000),
        totalRecDurationMS: Int = RandomFixture.randomInt(30_000, 60_000),
        singableRecDurationMS: Int = RandomFixture.randomInt(30_000, 60_000),
        totalScore: Float = RandomFixture.randomFloat() * 100,
        tuneScore: Float = RandomFixture.randomFloat() * 100,
        timingScore: Float = RandomFixture.randomFloat() * 100,
        detailScorePath: String = "{}",
        mixPath: String = UniqueFixture.newFile().absolutePath,
        metaPath: String = UniqueFixture.newFile().absolutePath,
        rawRecPath: String = UniqueFixture.newFile().absolutePath,
    ): SingScore = SingScore(
        songId = songId,
        songDurationMS = songDurationMS,
        totalRecDurationMS = totalRecDurationMS,
        singableRecDurationMS = singableRecDurationMS,
        totalScore = totalScore,
        tuneScore = tuneScore,
        timingScore = timingScore,
        reviewData = detailScorePath,
        mixPath = mixPath,
        metaPath = metaPath,
        rawRecPath = rawRecPath,
    )
}