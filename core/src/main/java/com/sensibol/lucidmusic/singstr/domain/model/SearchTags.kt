package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchTags(
    val id: String,
    val title: String,
    var isCheck: Boolean = false
) : Parcelable