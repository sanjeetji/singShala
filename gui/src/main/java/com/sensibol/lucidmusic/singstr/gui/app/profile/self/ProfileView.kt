package com.sensibol.lucidmusic.singstr.gui.app.profile.self

import android.os.Parcelable
import com.sensibol.lucidmusic.singstr.domain.model.Attempt
import com.sensibol.lucidmusic.singstr.domain.model.AttemptWithStatics
import com.sensibol.lucidmusic.singstr.domain.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileView(
    val id: String,
    val username: String,
    val name: String,
    val profileURL: String,
    val status: String,
    val location: String,
) : Parcelable

internal fun User.toProfileView(): ProfileView = ProfileView(
    id = id,
    username = handle,
    name = name,
    profileURL = dpUrl,
    status = status,
    location = city
)

@Parcelize
data class CoverView(
    val attemptId: String,
    val thumbnailUrl: String,
    val title: String,
    val coverUrl: String,
    val viewCount: Long
) : Parcelable

internal fun Attempt.toCoverView(): CoverView = CoverView(
    attemptId = id,
    thumbnailUrl = coverUrl,
    title = songTitle,
    coverUrl = publicMediaUrl,
    viewCount = 0L
)

internal fun AttemptWithStatics.toCoverView(): CoverView = CoverView(
    attemptId = id,
    thumbnailUrl = coverUrl,
    title = songTitle,
    coverUrl = publicMediaUrl,
    viewCount = statics.viewCount
)

