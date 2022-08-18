package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.ContestData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun ContestDataResponseById.toContestDataById() = ContestData(
    _id = _id,
    contestName = contestName,
    startDate = startDate,
    submissionEndDate = submissionEndDate,
    winnerAnnouncementDate = winnerAnnouncementDate,
    dashboardBannerContents = dashboardBannerContents,
    eligibilityLevel = eligibilityLevel,
    hostName = hostName,
    contestType = contestType,
    contestVideo = contestVideo,
    hostLogo = hostLogo,
    prize = prize,
    howToEnter = howToEnter,
    rules = rules,
    judgingCriteria = judgingCriteria,
    songList = songList

)

@JsonClass(generateAdapter = true)
data class ContestDataResponseById(
    @Json(name = "_id")
    val _id: String,
    @Json(name = "ContestName")
    val contestName: String,
    @Json(name = "StartDate")
    val startDate: String,
    @Json(name = "SubmissionEndDate")
    val submissionEndDate: String,
    @Json(name = "WinnerAnnouncementDate")
    val winnerAnnouncementDate: String,
    @Json(name = "DashboardBannerContents")
    val dashboardBannerContents: String,
    @Json(name = "EligibilityLevel")
    val eligibilityLevel: Int,
    @Json(name = "HostName")
    val hostName: String,
    @Json(name = "HostLogo")
    val hostLogo: String,
    @Json(name = "ContestType")
    val contestType: String,
    @Json(name = "ContestVideo")
    val contestVideo: String,
    @Json(name = "Prize")
    val prize: String,
    @Json(name = "HowToEnter")
    val howToEnter: String,
    @Json(name = "Rules")
    val rules: String,
    @Json(name = "JudgingCriteria")
    val judgingCriteria: String,
    @Json(name = "SongList")
    val songList: List<String>
)