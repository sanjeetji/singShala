package com.sensibol.lucidmusic.singstr.domain.model

data class SubmitsWithStatics(
    val attempt: List<AttemptWithStatics>
)

data class AttemptWithStatics(
    val id: String,
    val comment: String,
    val coverUrl: String,
    val publicMediaUrl: String,
    val songId: String,
    val songTitle: String,
    val statics: Cover.Statistics
)