package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContestData(
    val contestName: String,
    val contestType: String,
    val contestVideo: String,
    val dashboardBannerContents: String,
    val eligibilityLevel: Int,
    val hostLogo: String,
    val hostName: String,
    val howToEnter: String,
    val judgingCriteria: String,
    val prize: String,
    val rules: String,
    val songList: List<String>,
    val startDate: String,
    val submissionEndDate: String,
    val winnerAnnouncementDate: String,
    val _id: String
) : Parcelable