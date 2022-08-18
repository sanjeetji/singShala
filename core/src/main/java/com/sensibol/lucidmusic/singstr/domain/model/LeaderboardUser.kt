package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LeaderboardUser(
    val id: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val profileImg: String,
    val xp: String,
    val rank: Int,
    val level: Int,
) : Parcelable
