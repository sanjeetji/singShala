package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Exercise(
    override val id: String,
    val name: String,
    val subtitle: String,
    val status: String
) : Parcelable, Singable()
