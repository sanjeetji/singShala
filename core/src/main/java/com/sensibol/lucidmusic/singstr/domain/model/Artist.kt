package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Artist(
    val name: String,
) : Parcelable, Serializable

val List<Artist>.names: String get() = joinToString { it.name }